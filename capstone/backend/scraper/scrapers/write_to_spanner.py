from google.cloud import spanner

DATABASE_INSTANCE = 'capstone-instance'
DATABASE_ID = 'step39-db'
database = spanner.Client().instance(DATABASE_INSTANCE).database(DATABASE_ID)
items_cols = ['ItemId', 'ItemName', 'ItemBrand', 'ItemSubtype', 'ItemType']
inventories_cols = ['ItemId', 'ItemAvailability', 'Price', 'PPU', 'Unit', 'StoreId', 'LastUpdated']
store_cols = ['StoreId', 'Address', 'StoreName']

def write_item_info_to_items_table(item_id, item_name, item_brand, item_subtype, item_type):
  """
  Writes or updates an item to the Items table in the Spanner databse.
  """
  with database.batch() as batch:
    batch.insert_or_update(
      table = 'Items',
      columns=items_cols,
      values=[[item_id, item_name, item_brand, item_subtype, item_type]]
    )

def write_item_info_to_inventory_table(item_id, item_avalibility, price, ppu, unit, store_id):
  """
  Writes or updates an item to the Inventories table in the Spanner databse.
  """
  with database.batch() as batch:
    batch.insert_or_update(
      table='Inventories',
      columns=inventories_cols,
      values=[[item_id, item_avalibility, price, ppu, unit, store_id, spanner.COMMIT_TIMESTAMP]]
    )

def write_store_info_to_stores_table(store_id, address, store_name):
  """
  Writes or updates an Store to the Stores table in the Spanner databse.
  """
  with database.batch() as batch:
    batch.insert_or_update(
      table='Stores',
      columns=store_cols,
      values=[[store_id, address, store_name]]
    )