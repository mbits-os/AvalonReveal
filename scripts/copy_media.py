from __future__ import print_function

from common import *
import os
import sys

def copy_media(root_json, output_directory):
  root = JSON(root_json)

  copy_dir(
    os.path.join(os.path.dirname(root_json), 'avatars'),
    os.path.join(output_directory, 'res', 'drawable'),
    lambda name: 'ic_avatar_' + name.replace('-', '_'))

  if 'langs' in root:
    for lang in root['langs']:
      drawable = 'drawable' if lang == 'en' else 'drawable-' + lang
      raw = 'raw' if lang == 'en' else 'raw-' + lang
      copy_dir(
        os.path.join(os.path.dirname(root_json), lang, 'media'),
        os.path.join(output_directory, 'res', raw),
        lambda name: 'dialogue_' + name.replace('-', '_'))

if __name__ == '__main__':
  if len(sys.argv) < 3:
    print('copy_media.py INPUT.json OUTPUT-DIR')
    sys.exit(1)
  copy_media(sys.argv[1], sys.argv[2])
