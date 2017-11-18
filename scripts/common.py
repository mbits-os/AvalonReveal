from __future__ import print_function

import collections
import errno
import json
import os
import shutil
import sys

def mkdir(dirname):
  try:
      os.makedirs(dirname)
  except OSError as exc: 
      if exc.errno == errno.EEXIST and os.path.isdir(dirname):
          pass

def JSON_internal(fname):
  with open(fname) as f:
    return json.JSONDecoder(object_pairs_hook=collections.OrderedDict).decode(f.read())

def JSON(fname):
  global root_json
  root_json = fname
  return JSON_internal(fname)

def load_strings(lang):
  strings = collections.OrderedDict()
  data = JSON_internal(os.path.join(os.path.dirname(root_json), lang, os.path.basename(root_json)))
  if 'titles' in data:
    for key in data['titles']:
      base_name = 'titles__' + key.replace('-', '_').replace(':', '_') + '__'
      title = data['titles'][key]
      if isinstance(title, basestring):
        strings[base_name + 'name'] = title
      else:
        strings[base_name + 'name'] = title[0]
        strings[base_name + 'descr'] = title[1]
  if 'dialogs' in data:
    # strings['__actors__'] = collections.OrderedDict()
    for key in data['dialogs']:
      # strings['__actors__'][key] = 1
      base_name = 'dialogs__' + key.replace('-', '_').replace(':', '_') + '__'
      dlg = data['dialogs'][key]
      for d in dlg:
        end = ':' if key == 'all' and d == 'warning' else '.'
        strings[base_name + d.replace('-', '_').replace(':', '_')] = dlg[d] + end
  return strings

def copy_dir(srcdir, destdir, filter):
  rr = os.path.dirname(root_json) + os.sep
  mod = srcdir[len(rr):].split(os.sep)[0]
  print(' ', mod, ' ->', destdir)
  mkdir(destdir)
  for dirpath, dirnames, filenames in os.walk(srcdir):
    for inname in filenames:
      outname = filter(inname)
      if outname is None: continue
      shutil.copyfile(os.path.join(srcdir, inname), os.path.join(destdir, outname))
    break
