from __future__ import print_function

from common import *
import collections
import os
import shutil
import sys

from codecs import open

def build_strings(root_json, output_directory):
  root = JSON(root_json)

  langs = {}
  strings = collections.OrderedDict()

  if 'langs' in root:
    for lang in root['langs']:
      values = 'values' if lang == 'en' else 'values-' + lang
      langs[lang] = os.path.join(output_directory, 'res', values, os.path.splitext(os.path.basename(root_json))[0] + '.xml')
      print(' ', lang, ' ->', langs[lang])
      tr = load_strings(lang)
      for key in tr:
        if key not in strings:
          strings[key] = {}
        strings[key][lang] = tr[key]

  for lang in langs:
    fname = langs[lang]
    mkdir(os.path.dirname(fname))
    with open(fname, 'w', 'utf-8') as f:
      print('<resources>', file=f)
      for key in strings:
        s = strings[key]
        if lang in s:
          print(u'  <string name="{}">{}</string>'.format(key, s[lang]), file=f)
        else:
          print(u'  <string name="{}"/>'.format(key, val), file=f)
      print('</resources>', file=f)

if __name__ == '__main__':
  if len(sys.argv) < 3:
    print('strings.py INPUT.json OUTPUT-DIR')
    sys.exit(1)

  build_strings(sys.argv[1], sys.argv[2])
