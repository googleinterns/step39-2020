# Lint as: python3
"""
TODO(carolynlwang): Documentation and description.
"""

from bs4 import BeautifulSoup

print('__file__={0:<35} | __name__={1:<20} | __package__={2:<20}'.format(__file__,__name__,str(__package__)))

from .. import scraper
import mock
import requests
import unittest

_FAKE_TARGET_URL = 'http://walmart.com/search/?grid=false&query=milk&stores=2486'
_FAKE_HTML_RESPONSE = '<html>Empty</html>'
_FAKE_HTML_BODY= '''<html><script id="searchContent" type="application/json"> { 
  "searchContent": { 
    "preso": {
      "items": [
        { 
          "title": "fake"
        }
      ]
    }
  }
}
</script></html>'''
_FAKE_ITEMS = [ {"title": "fake"} ]

class FakeResponse(object):
  """Fake requests.Response object for requests.get()."""

  def __init__(self, status_code=None, text=None):
    self.status_code = status_code
    self.text = text
    self.content = text.encode('utf8')

  def get_page(self):
    return BeautifulSoup(self.text, 'html.parser')

class ScraperTest(unittest.TestCase):
  def test_get_page(self): 
    fake_response = FakeResponse(200, _FAKE_HTML_RESPONSE)
    soup = scraper.Scraper.get_page(_FAKE_TARGET_URL)
    self.assertEqual(soup, fake_response.get_page())

  def test_get_items(self):
    items = scraper.Scraper.get_items(BeautifulSoup(_FAKE_HTML_BODY, 'html.parser'))
    self.assertEqual(items, _FAKE_ITEMS)

if __name__ == '__main__':
  unittest.main()
