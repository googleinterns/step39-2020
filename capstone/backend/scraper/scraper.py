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
from retrying import retry

FLAGS = flags.FLAGS

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
    Finds and returns the items in JSON format.
    """
    results = soup.find('script', type='application/json', id='searchContent')
    if results is not None:
      data = json.loads(results.find(text=True).strip())
      return data['searchContent']['preso']['items']
    return []


  def get_item_info(item):
    """From the JSON data of the item,
    Finds and returns the attributes of the item
    for item purposes (itemId, itemName, 
    itemBrand, itemSubtype).
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
    Finds and returns the attributes of the item
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

    inventory_dict['LastUpdated'] = datetime.now().strftime('%y-%m-%d %H:%M:%S')

    # TODO(carolynlwang): About 20% of the items have prices listed in a min/max format.
    # Right now, their prices don't end up in the database.
    if 'primaryOffer' in item and 'offerPrice' in item['primaryOffer']:
        inventory_dict['Price'] = item['primaryOffer']['offerPrice']
    else:
        inventory_dict['Price'] = ''
    if 'ppu' in item:
      inventory_dict['PPU'] = item['ppu']['amount'] if 'amount' in item['ppu'] else ''
      inventory_dict['Unit'] = item['ppu']['unit'] if 'unit' in item['ppu'] else ''
    else:
      inventory_dict['PPU'] = ''
      inventory_dict['Unit'] = ''
   
    return inventory_dict

def main(argv):
  if len(argv) > 1:
    raise app.UsageError('Too many command-line arguments.')

  # Hard-coded item types.
  types = ['milk', 'paper+towels', 'water', 'cookies', 'pencil',
  'soda', 'cereal', 'chips', 'ketchup', 'flour', 'napkin',
  'ramen', 'shampoo', 'sugar', 'olive+oil']

  # Hard-coded store id, locations based on my own.
  stores = ['2486', '2119', '2280', '3123', '4174']

  # Column names for inventories
  inventories_cols = ['StoreId', 'ItemId', 'ItemAvailability', 'LastUpdated', 'Price', 'PPU', 'Unit']

  # Column names for items
  items_cols = ['ItemId', 'ItemName', 'ItemBrand', 'ItemType', 'ItemSubtype']
  
  # Writes item and inventory results into a csv file.
  # First, write column names.
  with open('inventories.csv', mode='w') as inventories_file:
    writer = csv.writer(inventories_file, delimiter=',')
    writer.writerow(inventories_cols)
  with open('items.csv', mode='w') as items_file:
    writer = csv.writer(items_file, delimiter=',')
    writer.writerow(items_cols)

  # Keep a list of unique ItemIds
  item_ids = []

  for store in stores:
    for type in types:
      soup = Scraper.get_page('https://www.walmart.com/search/?grid=false&query=' + type + '&stores=' + store)
      items = Scraper.get_items(soup)
      for item in items:
        # Check if we have recorded this item before.
        if 'productId' in item:
          if item['productId'] not in item_ids:
            item_ids.append(item['productId'])
            item_info = Scraper.get_item_info(item)
            item_info['ItemType'] = type
            with open('items.csv', mode='a+', newline='') as items_file:
              writer = csv.DictWriter(items_file, delimiter=',', fieldnames = items_cols)
              writer.writerow(item_info)

        inventory_info = Scraper.get_inventory_info(item)
        inventory_info['StoreId'] = store 
        with open('inventories.csv', mode='a+', newline='') as inventories_file:
          writer = csv.DictWriter(inventories_file, delimiter=',', fieldnames = inventories_cols)
          writer.writerow(inventory_info)

if __name__ == '__main__':
  app.run(main)
