# Lint as: python3
"""
Scrapes inventory data from the Walmart search result page
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
    data = json.loads(results.find(text=True).strip())
    return data['searchContent']['preso']['items']
	
  def get_item_info(item):
    """From the JSON data of the item, 
    Finds and returns the attributes of the item
    (right now, in the order of productId, itemAvailability, 
    timeUpdated, price, ppu, unit) as a list.
    """
    row = []
    row.append(item['productId']) if 'productId' in item else row.append('')
    if 'inventory' in item:
      if 'displayFlags' in item['inventory']:
        row.append(item['inventory']['displayFlags'][0]) 
      else: 
        row.append('AVAILABLE')
    else:
      row.append('')
    
    row.append(datetime.now().strftime('%y-%m-%d %H:%M:%S'))
			
    # TODO(carolynlwang): About 20% of the items have prices listed in a min/max format.
    # Right now, their prices don't end up in the database.   
    if 'primaryOffer' in item:
      if 'offerPrice' in item['primaryOffer']:
        row.append(item['primaryOffer']['offerPrice'])
      else:
        row.append('')   
    if 'ppu' in item:
      row.append(item['ppu']['amount']) if 'amount' in item['ppu']\
      else row.append('')
      row.append(item['ppu']['unit']) if 'unit' in item['ppu']\
      else row.append('')
    else:
      row.extend(['', ''])
   
    return row

def main(argv):
  if len(argv) > 1:
    raise app.UsageError('Too many command-line arguments.')
	
  # Hard-coded item types.
  types = ['milk', 'paper+towels', 'water', 'cookies', 'pencil',
  'soda', 'cereal', 'chips', 'ketchup', 'flour', 'napkin', 
  'ramen', 'shampoo', 'sugar', 'olive+oil']

  # Hard-coded store id, locations based on my own.
  stores = ['2486', '2119', '2280', '3123', '4174']
	

  """
  Writes results into a csv file.
  """
  # Write column names
  with open('inventory.csv', mode='w') as inventory_file:	
    writer = csv.writer(inventory_file, delimiter=',')
    writer.writerow(['StoreId', 'ItemId', 'ItemAvailability', 'LastUpdated', 'Price', 'Ppu', 'Unit'])
	
  for store in stores:
    for type in types:
      soup = Scraper.get_page('https://www.walmart.com/search/?grid=false&query=' + type + '&stores=' + store)
      items = Scraper.get_items(soup)
      for item in items:
        info = [store] + Scraper.get_item_info(item)
        with open('inventory.csv', mode='a+', newline='') as inventory_file:
          writer = csv.writer(inventory_file, delimiter=',')
          writer.writerow(info)

if __name__ == '__main__':
  app.run(main)
