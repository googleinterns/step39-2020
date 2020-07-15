import unittest
from .. import get_items, get_store_address, get_product_id
from bs4 import BeautifulSoup

FAKE_PAGE_SOURCE = '''
<span class="reserve-nav__current-instore-text">11050 Bollinger Canyon Rd</span>
<product-item>
  <span class="product-title">Lucerne Milk Whole 1 Gallon - 128 Fl. Oz.</span>
  <span class="product-price">Your Price</span>$4.58</span>
  <span class="product-price-qty"> ($0.04 / Fl.oz) </span>
</product-item>
<product-item>
  <span class="product-title">A2 Whole Milk - Half Gallon</span>
  <span class="product-price">Your Price</span>$4.99</span>
  <span class="product-price-qty"> ($0.09 / Fl.oz) </span>
</product-item>
'''
FAKE_PAGE_SOURCE_INCORRECT = '''
<span class="reserve-nav__current-instore-text">11050 Bollinger Canyon Rd</span>
<product-item>
  <span class="product-title">Lucerne Milk Whole 1 G.</span>
  <span class="product-price">Your Price</span>4.58</span>
  <span class="product-price-qty"> ($0.04Fl.oz) </span>
</product-item>
<product-item>
  <span class="product-title">A2 Wh</span>
  <span class="product-price">Your Price</span>$4.9.9.</span>
  <span class="product-price-qty"> ($0.09Fl.oz) </span>
</product-item>
'''
fake_items = [{
  'name': 'Lucerne Milk Whole 1 Gallon - 128 Fl. Oz.',
  'price': 4.58,
  'ppu': 0.04,
  'unit': 'Fl.oz',
  'avalibilty': 'AVAILABLE'
}, {
  'name': 'A2 Whole Milk - Half Gallon',
  'price': 4.99,
  'ppu': 0.09,
  'unit': 'Fl.oz',
  'avalibilty': 'AVAILABLE'
}]

class TestSafewayScraper(unittest.TestCase):
  def test_get_items(self):
    soup = BeautifulSoup(FAKE_PAGE_SOURCE, 'html.parser')
    items = get_items(soup)
    self.assertEquals(items, fake_items)

  def test_get_items_incorrect_page_source(self):
    soup = BeautifulSoup(FAKE_PAGE_SOURCE_INCORRECT, 'html.parser')
    items = get_items(soup)
    self.assertEquals(items, [])

  def test_get_store_address(self):
    soup = BeautifulSoup(FAKE_PAGE_SOURCE, 'html.parser')
    address = get_store_address(soup)
    self.assertEquals(address, '11050 Bollinger Canyon Rd')

  def test_get_product_id(self):
    id = get_product_id(' Lucerne Milk Whole 1 Gallon - 128 Fl. Oz.')
    self.assertEquals(id, '-2030711286941086991')

if __name__ == '__main__':
  unittest.main()