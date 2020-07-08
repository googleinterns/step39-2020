# Lint as: python3
"""
Scrapes inventory and items data
from the Walmart search result page
using BeautifulSoup.
"""

import csv, json, requests
from absl import app
from absl import flags
from bs4 import BeautifulSoup
from datetime import datetime

FLAGS = flags.FLAGS

class Scraper:
  def get_page(URL, retries=3):
    """Given a URL, scrapes the web page.
    Retries three times (unless specified otherwise).
    """
    try:
      page = requests.get(URL)
      soup = BeautifulSoup(page.content, 'html.parser')
      return soup
    except ValueError as err:
      print(err)
      if retries < 1:
        raise ValueError('No more retries')
      return get(URL, retries - 1)

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
    for item purposes (in the order of itemId,
    itemName, itemBrand, itemSubtype).
    """
    item_row = []
    item_row.append(item['productId']) if 'productId' in item else item_row.append('')
    item_row.append(item['title'].replace('<mark>', '').replace('</mark>', ''))\
    if 'title' in item else item_row.append('')
    item_row.append(item['brand'][0]) if 'brand' in item else item_row.append('')
    item_row.append(item['seeAllName']) if 'seeAllName' in item else item_row.append('')
    return item_row

  def get_inventory_info(item):
    """From the JSON data of the item,
    Finds and returns the attributes of the item
    for inventory purposes (in the order of itemId,
    itemAvailability, timeUpdated, price, ppu, unit).
    """
    inventory_row = []
    inventory_row.append(item['productId']) if 'productId' in item else inventory_row.append('')
    if 'inventory' in item:
      if 'displayFlags' in item['inventory']:
        inventory_row.append(item['inventory']['displayFlags'][0])
      else:
        inventory_row.append('AVAILABLE')
    else:
      inventory_row.append('')

    inventory_row.append(datetime.now().strftime('%y-%m-%d %H:%M:%S'))

    # TODO(carolynlwang): About 20% of the items have prices listed in a min/max format.
    # Right now, their prices don't end up in the database.
    if 'primaryOffer' in item:
      if 'offerPrice' in item['primaryOffer']:
        inventory_row.append(item['primaryOffer']['offerPrice'])
      else:
        inventory_row.append('')
    if 'ppu' in item:
      inventory_row.append(item['ppu']['amount']) if 'amount' in item['ppu']\
      else inventory_row.append('')
      inventory_row.append(item['ppu']['unit']) if 'unit' in item['ppu']\
      else inventory_row.append('')
    else:
      inventory_row.extend(['', ''])
   
    return inventory_row

def main(argv):
  if len(argv) > 1:
    raise app.UsageError('Too many command-line arguments.')

  # Hard-coded item types.
  types = ['milk', 'paper+towels', 'water', 'cookies', 'pencil',
  'soda', 'cereal', 'chips', 'ketchup', 'flour', 'napkin',
  'ramen', 'shampoo', 'sugar', 'olive+oil']

  # Hard-coded store id, locations based on my own.
  stores = ['2486', '2119', '2280', '3123', '4174']

  # Writes item and inventory results into a csv file.
  # First, write column names.
  with open('inventories.csv', mode='w') as inventories_file:
    writer = csv.writer(inventories_file, delimiter=',')
    writer.writerow(['StoreId', 'ItemId', 'ItemAvailability', 'LastUpdated', 'Price', 'Ppu', 'Unit'])
  with open('items.csv', mode='w') as items_file:
    writer = csv.writer(items_file, delimiter=',')
    writer.writerow(['ItemId', 'ItemName', 'ItemBrand', 'ItemSubtype', 'ItemType'])

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
            item_info = item_info + [type]
            with open('items.csv', mode='a+', newline='') as items_file:
              writer = csv.writer(items_file, delimiter=',')
              writer.writerow(item_info)

        inventory_info = Scraper.get_inventory_info(item)
        inventory_info = [store] + inventory_info
        with open('inventories.csv', mode='a+', newline='') as inventories_file:
          writer = csv.writer(inventories_file, delimiter=',')
          writer.writerow(inventory_info)

if __name__ == '__main__':
  app.run(main)
