""" 
Scrapes inventory and items data from the Safeway search 
result page using Selenium and BeautifulSoup. 
"""

import csv
import hashlib
import logging
import sys
import threading
from bs4 import BeautifulSoup
from google.cloud import spanner
from selenium import webdriver
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from write_to_spanner import write_item_info_to_inventory_table, write_item_info_to_items_table, write_store_info_to_stores_table

AVALIBILITY_KEY = 'availability'
CHROME_DRIVER_PATH = '/Users/anudeepyakkala/Downloads/chromedriver'
ID_KEY = 'id'
LOG_FILE_NAME = 'safeway_scraper.log'
NAME_KEY = 'name'
PPU_KEY = 'ppu'
PRICE_KEY = 'price'
UNIT_KEY = 'unit'
write_to_spanner = True
logging.basicConfig(filename=LOG_FILE_NAME, level=logging.ERROR)


def get_item_page_html(item_type, zipcode):
  """
  Obtains the page source for the page containing all the items
  of the specified item_type for the store in the specified zipcode.
  """
  try:
    base_url = 'https://www.safeway.com/shop/search-results.html?q=' + item_type
    driver = webdriver.Chrome(executable_path=CHROME_DRIVER_PATH)
    driver.get(base_url)
    WebDriverWait(driver, 20).until(EC.visibility_of_element_located((By.XPATH, '//*[@id="openFulfillmentModalButton"]')))
    driver.find_element_by_xpath('//*[@id="openFulfillmentModalButton"]').click()
    WebDriverWait(driver, 20).until(EC.visibility_of_element_located((By.XPATH, '//*[@id="storeFulfillmentModal"]/div/div/div[2]/input')))
    if (driver.find_element_by_xpath('//*[@id="onboardingModal"]/div[2]/div[2]/div/div[2]').is_displayed()):
      driver.find_element_by_xpath('//*[@id="onboardingCloseButton"]').click()
    driver.find_element_by_xpath('//*[@id="storeFulfillmentModal"]/div/div/div[2]/input').send_keys(zipcode)
    driver.find_element_by_xpath('//*[@id="storeFulfillmentModal"]/div/div/div[2]/span').click()
    WebDriverWait(driver, 20).until(EC.visibility_of_element_located((By.XPATH, '//*[@id="fulfilmentInStore"]/div/div/div[1]/store-card-unified/div[2]/div/a')))
    driver.find_element_by_xpath('//*[@id="fulfilmentInStore"]/div/div/div[1]/store-card-unified/div[2]/div/a').click()
    WebDriverWait(driver, 20).until(EC.visibility_of_all_elements_located((By.XPATH, '//*[@id="addButton"]')))
    soup = BeautifulSoup(driver.page_source, 'html.parser')
    driver.quit()
    return soup
  except TimeoutException:
    logging.error('Timed out when trying to obtain the item page for ' + item_type + '.')
    driver.quit() 
    return None

def get_items(soup, item_type):
  """
  Obtains item information for each of the items from the provided 
  page source.
  """
  if (soup == None):
    return []
  items = []
  for item in soup.findAll('product-item'):
    try: 
      ppuString = item.find('span', {'class': 'product-price-qty'}).text.replace(',', '')
      current = {
        NAME_KEY: item.find('a', {'class': 'product-title'}).text,
        PRICE_KEY: float(item.find("span", {'class': 'product-price'}).text[11:]),
        PPU_KEY: float(ppuString[ppuString.index('$') + 1 : ppuString.index('/') - 1]),
        UNIT_KEY: ppuString[ppuString.index('/') + 2 : ppuString.index(')')],
        AVALIBILITY_KEY: 'AVAILABLE'
      }
      items.append(current)
    except Exception as e:
      logging.error('Unable to parse items from page source for ' + item_type + '.')
  return items

def get_product_id(item_name, zipcode):
  """
  Returns the productId for a specific item.
  """
  s = 'Safeway' + zipcode + item_name
  return int(hashlib.sha1(s.encode('utf-8')).hexdigest(), 16) % (10 ** 8)

def get_store_address(soup):
  """
  Returns address of the store given the page source.
  """
  address = soup.find('span', {'class': 'reserve-nav__current-instore-text'}).text
  return address

def scrape_store(zipcode):
  # Hard-coded item types.
  item_types = ['milk', 'paper towels', 'water', 'cookies', 'pencil',
  'soda', 'cereal', 'chips', 'ketchup', 'flour', 'napkin',
  'ramen', 'shampoo', 'sugar', 'olive oil']

  address = get_store_address(get_item_page_html('', zipcode))
  store_id = int(hashlib.sha1(address.encode('utf-8')).hexdigest(), 16) % (10 ** 8)
  write_store_info_to_stores_table(store_id, address, 'Safeway - ' + zipcode)
  for item_type in item_types:
    soup = get_item_page_html(item_type,zipcode)
    items = get_items(soup, item_type)
    for item in items:
      item[ID_KEY] = get_product_id(item[NAME_KEY], zipcode)
      if (write_to_spanner):
        write_item_info_to_items_table(item[ID_KEY], item[NAME_KEY], '', '', item_type)
        write_item_info_to_inventory_table(item[ID_KEY], item[AVALIBILITY_KEY], item[PRICE_KEY], \
          item[PPU_KEY], item[UNIT_KEY], store_id)
      f = open('csv/safeway-' + zipcode + '.csv', 'a')
      writer = csv.DictWriter(f, item.keys())
      writer.writerow(item)
      f.close()

if __name__ == "__main__":
  if (len(sys.argv) > 1) and (str(sys.argv[1]) == 'test'):
    write_to_spanner = False

  # Hard-coded store zip codes.
  zipcodes = ['94582', '95014']
  for zipcode in zipcodes:
    thread = threading.Thread(target=scrape_store, args=(zipcode,))
    thread.start()