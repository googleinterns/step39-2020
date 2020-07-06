# Lint as: python3
"""
Scrapes inventory data from the Walmart search result page
and adds it to a .csv file.
"""

import csv, json, requests
from absl import app
from absl import flags
from bs4 import BeautifulSoup
from datetime import datetime

FLAGS = flags.FLAGS

class Scraper:
	# Hard-coded item types.
	types = ['milk', 'paper+towels', 'water', 'cookies', 'pencil',
	'soda', 'cereal', 'chips', 'ketchup', 'flour', 'napkin', 
	'ramen', 'shampoo', 'sugar', 'olive+oil']

	stores = ['2485', '2119', '2280', '3123', '4174']
	def scrapeInventory():
		# Hard-coded store id, locations based on my own.
		#stores = ['2486', '2119', '2280', '3123', '4174']

		# Write column names
		with open('inventory.csv', mode='w') as inventory_file:
			writer = csv.writer(inventory_file, delimiter=',')
			writer.writerow(['StoreId', 'ItemId', 'ItemAvailability', 'LastUpdated', 'Price', 'Ppu', 'Unit'])

		for store in stores:
			for type in types:
				URL = 'https://www.walmart.com/search/?grid=false&query=' + type + '&stores=' + store
				page = requests.get(URL)
				soup = BeautifulSoup(page.content, 'html.parser')
				results = soup.find('script', type='application/json', id='searchContent')
				data = json.loads(results.find(text=True).strip())
				items = data['searchContent']['preso']['items']
				
				for item in items:	
					row = [store]
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
					with open('inventory.csv', mode='a+', newline='') as inventory_file:
						writer = csv.writer(inventory_file, delimiter=',')
						writer.writerow(row)

