import G
import strings
import copy_media
import os

def prepare(root_json, output_directory, package, packageR):
  prj_root = os.path.dirname(os.path.dirname(__file__))
  dot_root_json = os.path.join(prj_root, '.' + root_json)
  root_json = os.path.join(prj_root, root_json)
  output_directory = os.path.join(prj_root, output_directory)
  if not os.path.isfile(dot_root_json):
    dot_root_json = root_json

  G.generate_game_java(root_json, output_directory, package, packageR)
  strings.build_strings(root_json, output_directory)
  copy_media.copy_media(dot_root_json, output_directory)

if __name__ == '__main__':
  prepare(os.path.join('game', 'avalon.json'),
    os.path.join('android', 'app', 'src', 'main'),
    'com.midnightbits.avalonreveal.avalon',
    'com.midnightbits.avalonreveal')
