# Lint as: python3
"""
Scrapes inventory and items data
from the Walmart search result page
using BeautifulSoup.
To run: 
python3 scraper.py
"""

import csv, json, requests
from absl import app
from absl import flags
from bs4 import BeautifulSoup
from datetime import datetime
from google.cloud import spanner
from retrying import retry

FLAGS = flags.FLAGS

_DATABASE_INSTANCE = 'capstone-instance'
_DATABASE_ID = 'step39-db'

class Scraper:
  @retry(stop_max_attempt_number=3)
  def get_page(URL):
    """Given a URL, scrapes the web page.
    Retries three times (unless specified otherwise).
    """
    try:
      page = requests.get(URL)
      soup = BeautifulSoup(page.content, 'html.parser')
      return soup
    except ValueError as err:
      print(err)

  def get_items(soup):
    """
    Given a Walmart search result page's scrape results,
    finds and returns the items in JSON format.
    """
    results = soup.find('script', type='application/json', id='searchContent')
    if results is not None:
      data = json.loads(results.find(text=True).strip())
      return data['searchContent']['preso']['items']
    return []


  def get_item_info(item):
    """From the JSON data of the item,
    finds and returns the attributes of the item
    for item purposes (itemId, itemName, itemBrand, itemSubtype).
    """
    item_dict = {}
    item_dict['ItemId'] = item['productId'] if 'productId' in item else ''
    item_dict['ItemName'] = item['title'].replace('<mark>', '').replace('</mark>', '')\
    if 'title' in item else ''
    if 'brand' in item and len(item['brand']) > 0:
      item_dict['ItemBrand'] = item['brand'][0]
    else:
      item_dict['ItemBrand'] = ''
    item_dict['ItemSubtype'] = item['seeAllName'] if 'seeAllName' in item else ''
    return item_dict

  def get_inventory_info(item):
    """From the JSON data of the item,
    finds and returns the attributes of the item 
    for inventory purposes (itemId, itemAvailability, 
    timeUpdated, price, ppu, unit).
    """
    inventory_dict = {}
    inventory_dict['ItemId'] = item['productId'] if 'productId' in item else ''
    if 'inventory' in item:
      if 'displayFlags' in item['inventory']:
        inventory_dict['ItemAvailability'] = item['inventory']['displayFlags'][0]
      else:
        inventory_dict['ItemAvailability'] = 'AVAILABLE'
    else:
      inventory_dict['ItemAvailability'] = ''

    # TODO(carolynlwang): About 20% of the items have prices listed in a min/max format.
    # Right now, their prices don't end up in the database.
    if 'primaryOffer' in item and 'offerPrice' in item['primaryOffer']:
      inventory_dict['Price'] = float(item['primaryOffer']['offerPrice'])
    else:
        inventory_dict['Price'] = None
    if 'ppu' in item:
      inventory_dict['PPU'] = item['ppu']['amount'] if 'amount' in item['ppu'] else None
      inventory_dict['Unit'] = item['ppu']['unit'] if 'unit' in item['ppu'] else ''
    else:
      inventory_dict['PPU'] = None
      inventory_dict['Unit'] = ''
   
    return inventory_dict

def set_items_unavailable_default(transaction):
  """ Sets all item availabilities to OUT_OF_STOCK.
  This function marks items that no longer appear 
  in stores out of stock, as opposed to their old 
  availability statuses."""
  transaction.execute_update(
    "UPDATE Inventories "
    "SET ItemAvailability = \'OUT_OF_STOCK\' "
    "WHERE StoreId IN UNNEST(@stores)",
    params={'stores': [2486, 2119, 2280, 3123, 4174]},
    param_types={'stores': spanner.param_types.Array(spanner.param_types.INT64)}
  )

def main(argv):
  if len(argv) > 1:
    raise app.UsageError('Too many command-line arguments.')

  # Hard-coded item types.
  types = ['milk', 'paper towels', 'water', 'cookies', 'pencil',
  'soda', 'cereal', 'chips', 'ketchup', 'flour', 'napkin',
  'ramen', 'shampoo', 'sugar', 'olive oil']

  # Hard-coded store id, locations based on my own.
  # The store information needs to exist in Spanner
  # in order for batch writing of Inventories to work. 
  stores = ['2486', '2119', '2280', '3123', '4174']

  """ Important to note: dict values are ordered 
  by when they were put in (most recent last). 
  So for the batch.replace() to work, you need 
  the order of the items in this list to match the order 
  that you put the information into the dictionary 
  (e.g., if the last item you add to the dictionary is 
  timeUpdated, it also needs to be the last item in this list
  of column names).
  """
  # Column names for inventories
  inventories_cols = ['ItemId', 'ItemAvailability', 'Price', 'PPU', 'Unit', 'StoreId', 'LastUpdated']

  # Column names for items
  items_cols = ['ItemId', 'ItemName', 'ItemBrand', 'ItemSubtype', 'ItemType']

  # Instantiate a client for read/write.
  spanner_client = spanner.Client()

  # Get a Cloud Spanner instance by ID.
  instance = spanner_client.instance(_DATABASE_INSTANCE)

  # Get a Cloud Spanner database by ID.
  database = instance.database(_DATABASE_ID)

  # Set all items to default OUT_OF_STOCK.
  database.run_in_transaction(set_items_unavailable_default)

  # Keep a list of unique ItemIds
  item_ids = []

  for store in stores:
    for type in types:
      soup = Scraper.get_page('https://www.walmart.com/search/?grid=false&query=' + type.replace(' ', '+') + '&stores=' + store)
      items = Scraper.get_items(soup)
      for item in items:
        # If the price is null, do not record the item.
        if 'primaryOffer' in item and 'offerPrice' in item['primaryOffer']:
          # Check if we have recorded this item in another store before.
          if 'productId' in item:
            if item['productId'] not in item_ids:
              item_ids.append(item['productId'])
              item_info = Scraper.get_item_info(item)
              item_info['ItemType'] = type
              with database.batch() as batch:
                """ batch.replace() inserts or updates one or more 
                records in a table. Existing rows have values
                for supplied columns overwritten; ther column values
                set to null. 
                """
                batch.replace(
                  table = 'Items',
                  columns = items_cols,
                  values = [
                    item_info.values()
                  ]
                )
        
          inventory_info = Scraper.get_inventory_info(item)
          inventory_info['StoreId'] = int(store)
          inventory_info['LastUpdated'] = spanner.COMMIT_TIMESTAMP
          with database.batch() as batch:
            batch.replace(
              table = 'Inventories',
              columns = inventories_cols,
              values = [
                inventory_info.values()
              ]
            )

if __name__ == '__main__':
  app.run(main)
