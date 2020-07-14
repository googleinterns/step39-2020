import hashlib
from bs4 import BeautifulSoup
from google.cloud import spanner
from selenium import webdriver
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

CHROME_DRIVER_PATH = '/Users/anudeepyakkala/Downloads/chromedriver'
DATABASE_INSTANCE = 'capstone-instance'
DATABASE_ID = 'step39-db'
database = spanner.Client().instance(DATABASE_INSTANCE).database(DATABASE_ID)
items_cols = ['ItemId', 'ItemName', 'ItemBrand', 'ItemSubtype', 'ItemType']
inventories_cols = ['ItemId', 'ItemAvailability', 'Price', 'PPU', 'Unit', 'StoreId', 'LastUpdated']
store_cols = ['StoreId', 'Address', 'StoreName']


def get_item_page_html(item_type, zipcode):
  try:
    base_url = 'https://www.safeway.com/shop/search-results.html?q=' + item_type
    driver = webdriver.Chrome(executable_path='/Users/anudeepyakkala/Downloads/chromedriver')
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
    driver.quit() 
    return None

def get_items(soup):
  if (soup == None):
    return []
  items = []
  for item in soup.findAll('product-item'):
    try: 
      ppuString = item.find("span", {"class": "product-price-qty"}).text.replace(',', '')
      current = {
        "name": item.find("a", {"class": "product-title"}).text,
        "price": float(item.find("span", {"class": "product-price"}).text[11:]),
        "ppu": float(ppuString[ppuString.index('$') + 1 : ppuString.index('/') - 1]),
        "unit": ppuString[ppuString.index('/') + 2 : ppuString.index(')')],
        "itemAvalibility": 'AVALIABLE'
      }
      items.append(current)
    except:
      pass
  return items

def item_exists(item_id):
  with database.snapshot() as snapshot:
    results = snapshot.execute_sql(
      'SELECT * FROM Items '
      'WHERE ItemId = @itemId',
      params={"itemId": item_id},
      param_types={spanner.param_types.INT64}
    )
  return len(results) > 0

def get_product_id(item_name, zipcode):
  s = 'Safeway' + zipcode + item_name
  return int(hashlib.sha1(s.encode('utf-8')).hexdigest(), 16) % (10 ** 8)

def get_store_address(soup):
  address = soup.find('span', {'class': 'reserve-nav__current-instore-text'}).text
  return address


def write_item_info_to_items_table(item_id, item_name, item_brand, item_subtype, item_type):
  with database.batch() as batch:
    batch.insert_or_update(
      table = 'Items',
      columns=items_cols,
      values=[[item_id, item_name, item_brand, item_subtype, item_type]]
    )

def write_item_info_to_inventory_table(item_id, item_avalibility, price, ppu, unit, store_id):
  with database.batch() as batch:
    batch.insert_or_update(
      table='Inventories',
      columns=inventories_cols,
      values=[[item_id, item_avalibility, price, ppu, unit, store_id, spanner.COMMIT_TIMESTAMP]]
    )

def write_store_info_to_stores_table(store_id, address, store_name):
  with database.batch() as batch:
    batch.insert_or_update(
      table='Stores',
      columns=store_cols,
      values=[[store_id, address, store_name]]
    )

if __name__ == '__main__':
  # Hard-coded item types.
  item_types = ['milk', 'paper towels', 'water', 'cookies', 'pencil',
  'soda', 'cereal', 'chips', 'ketchup', 'flour', 'napkin',
  'ramen', 'shampoo', 'sugar', 'olive oil']

  # Hard-coded store zip codes.
  zipcodes = ['94582', '95014']

  for zipcode in zipcodes:
    address = get_store_address(get_item_page_html('', zipcode))
    store_id = int(hashlib.sha1(address.encode('utf-8')).hexdigest(), 16) % (10 ** 8)
    write_store_info_to_stores_table(store_id, address, 'Safeway - ' + zipcode)
    for item_type in item_types:
      soup = get_item_page_html(item_type,zipcode)
      items = get_items(soup)
      for item in items:
        item['id'] = get_product_id(item['name'], zipcode)
        write_item_info_to_items_table(item['id'], item['name'], '', '', item_type)
        write_item_info_to_inventory_table(item['id'], item['itemAvalibility'], item['price'], item['ppu'], item['unit'], store_id)

