# Lint as: python3
"""
Sets up a cron job for scraper.py
To run: 
python3 cron.py
"""
from absl import app
from crontab import CronTab

def main(argv):
  if len(argv) > 1:
    raise app.UsageError('Too many command-line arguments.')

  # Set up cron job.
  cron = CronTab(user = 'carolynlwang')
  job = cron.new(command = 'scraper.py')
  # Scrape daily
  job.hour.on(0)
  cron.write()

if __name__ == '__main__':
  app.run(main)