from __future__ import print_function

from common import *
import collections
import os
import sys

def generate_game_java(root_json, output_directory, package, packageR):
  gen = generator(root_json, output_directory, package, packageR)
  gen.read_data()
  gen.build_cards()
  gen.write_G_java()
  gen.write_ActorUtils_java()
  gen.write_DialogUtils_java()

def print_enum(f, name, values):
  print('''    public enum {} {{
        {}
    }}'''.format(name, ',\n        '.join(values)), file = f)

def print_container(f, name, type, values, conv):
  print('    static final class {} {{'.format(name), file = f);
  for v in values:
    print('        static final {} {} = {};'.format(type, v, conv(v)), file = f)
  print('    }', file = f);

def new_Text(name):
  actor, action = name.split('__')
  return 'new Text(actor.{}, action.{}, R.string.dialogs__{})'.format(actor, action, name)

#################################################################################################
#################################################################################################

class generator:
  def __init__(self, root_json, output_directory, package, packageR):
    self.root = JSON(root_json)
    self.package_dir = os.path.join(output_directory, 'java', *package.split('.'))
    self.package = package
    self.packageR = packageR

    print(output_directory)
    print('  ->', self.package_dir)
    print('  ->', self.packageR + '.R')
    print('  ->', self.package + '.G')

    self.lines = []
    self.keys_titles = collections.OrderedDict()
    self.actors = []
    self.actions = []
    self.dialogs = []
    self.deps = {}

  #################################################################################################
  #################################################################################################

  def read_data(self):
    self.dialogs = list(self.root['dialogs'].keys()) if 'dialogs' in self.root else []

    self.keys_titles = collections.OrderedDict()
    keys_dialogs = collections.OrderedDict()
    actors = collections.OrderedDict()
    actions = collections.OrderedDict()

    if 'langs' in self.root:
      for lang in self.root['langs']:
        tr = load_strings(lang)
        for key in tr:
          chunks = key.split('__')
          if chunks[0] == 'dialogs':
            actors[chunks[1]] = 1
            actions[chunks[2]] = 1
            keys_dialogs[chunks[1] + '__' + chunks[2]] = 1
          elif chunks[0] == 'titles':
            self.keys_titles[key] = 1

    self.deps = {}
    if 'dependency' in self.root:
      for dependant in self.root['dependency']:
        required = self.root['dependency'][dependant]
        if isinstance(required, basestring):
          if required not in self.deps:
            self.deps[required] = {}
          self.deps[required][dependant] = 1
        else:
          for req in required:
            if req not in self.deps:
              self.deps[req] = {}
            self.deps[req][dependant] = 1

    self.lines = keys_dialogs.keys()
    self.actors = actors.keys()
    self.actions = actions.keys()

  #################################################################################################
  #################################################################################################

  def next_id(self):
    s = str(self.line_id) + ', '
    self.line_id += 1
    return s

  def build_cards(self):
    whole_script = {}
    if 'base' in self.root:
      for dialogue in self.root['base']:
        lineno, actor, action = dialogue.split(':')
        whole_script[int(lineno)] = [(None, actor, action)]

    if 'dialogs' in self.root:
      dlgs = self.root['dialogs']
      for key in dlgs:
        for dialogue in dlgs[key]:
          lineno, actor, action = dialogue.split(':')
          lineno = int(lineno)
          if lineno not in whole_script:
            whole_script[lineno] = []
          whole_script[lineno].append((key, actor, action))

    self.line_id = 1

    for lineno in sorted(whole_script.keys()):
      texts = whole_script[lineno]
      texts = [
        self.next_id() + ('' if text[0] is None else 'dialog.' + text[0] + ', ') + u'text.' \
        + text[1].replace('-', '_').replace(':', '_') + u'__' \
        + text[2].replace('-', '_').replace(':', '_') for text in texts
      ]
      if len(texts) == 1:
        texts = '            new SingleLine(' + texts[0] + ')'
      else:
        texts = '            new MultiLine(' + ','.join(['\n                new DialogItem((1 << 16) + ' + text + ')' for text in texts]) + '\n            )'
      whole_script[lineno] = texts

    groups = self.root['groups'] if 'groups' in self.root else []
    self.cards = [[]]
    for i in range(len(groups)): self.cards.append([])

    refid = 0
    for lineno in sorted(whole_script.keys()):
      while refid < len(groups) and lineno >= groups[refid]: refid += 1
      self.cards[refid].append(whole_script[lineno])

  #################################################################################################
  #################################################################################################

  def new_Option(self, name):
    base = 'titles__{}__'.format(name)
    descr = base + 'descr'
    descr = ', R.string.{}'.format(descr) if descr in self.keys_titles else ''
    dep = self.deps[name] if name in self.deps else {}
    dep = ''.join([', dialog.' + n for n in dep])
    return 'new Option(dialog.{}, R.string.{}name{}{})'.format(name, base, descr, dep)

  #################################################################################################
  #################################################################################################
  #################################################################################################
  #################################################################################################
  #################################################################################################

  def write_G_java(self):
    mkdir(self.package_dir)
    with open(os.path.join(self.package_dir, 'G.java'), 'w') as f:
      importR = '' if self.package == self.packageR else '\nimport {}.R;\n'.format(self.packageR)
      print('''/* AUTO-GENERATED FILE. DO NOT MODIFY.
*
* This class was automatically generated by the
* tr2strings tool from the game data it found. It
* should not be modified by hand.
*/

package {};
{}
public final class G {{'''.format(self.package, importR), file = f)
      print_enum(f, 'dialog', ['base'] + self.dialogs + ['warnings'])
      print(file = f)
      print_enum(f, 'actor', self.actors)
      print(file = f)
      print_enum(f, 'action', self.actions)
      print(file = f)
      print_container(f, 'option', 'Option', self.dialogs, self.new_Option)
      print(file = f)
      print('''    public static final Option[] options = {{
{}
    }};'''.format(',\n'.join(['        option.' + dlg for dlg in self.dialogs])), file = f)
      print(file = f)
      print_container(f, 'text', 'Text', self.lines, new_Text)
      print(file = f)
      print('    static final Line[][] lines = {', file = f)
      print('        {', file = f)
      print('\n        },\n        {\n'.join([',\n'.join(card) for card in self.cards]), file = f)
      print('        }', file = f)
      print('    };', file = f)
      print('}', file = f)

  #################################################################################################
  #################################################################################################
  #################################################################################################
  #################################################################################################
  #################################################################################################

  def write_ActorUtils_java(self):
    mkdir(self.package_dir)
    with open(os.path.join(self.package_dir, 'ActorUtils.java'), 'w') as f:
      importR = '' if self.package == self.packageR else '\nimport {}.R;\n'.format(self.packageR)
      print('''/* AUTO-GENERATED FILE. DO NOT MODIFY.
*
* This class was automatically generated by the
* tr2strings tool from the game data it found. It
* should not be modified by hand.
*/

package {};

import android.support.annotation.DrawableRes;
{}
import static com.midnightbits.avalonreveal.avalon.G.actor.*;

public final class ActorUtils {{
    @DrawableRes
    public static int avatar(G.actor actor) {{
        switch (actor) {{'''.format(self.package, importR), file = f)
      for actor in self.actors:
        print('            case {0}: return R.drawable.ic_avatar_{0};'.format(actor), file = f)
      print('''        }
        return 0;
    }
}''', file = f)

  #################################################################################################
  #################################################################################################
  #################################################################################################
  #################################################################################################
  #################################################################################################

  def write_DialogUtils_java(self):
    mkdir(self.package_dir)
    with open(os.path.join(self.package_dir, 'DialogUtils.java'), 'w') as f:
      importR = '' if self.package == self.packageR else '\nimport {}.R;\n'.format(self.packageR)
      print('''/* AUTO-GENERATED FILE. DO NOT MODIFY.
*
* This class was automatically generated by the
* tr2strings tool from the game data it found. It
* should not be modified by hand.
*/

package {};

import android.support.annotation.DrawableRes;
{}
import static com.midnightbits.avalonreveal.avalon.G.actor.*;

public final class DialogUtils {{
    @DrawableRes
    public static int avatar(G.dialog dialog) {{
        switch (dialog) {{'''.format(self.package, importR), file = f)
      for dialog in self.dialogs:
        print('            case {0}: return R.drawable.ic_avatar_{0};'.format(dialog), file = f)
      print('''            default: break;
        }
        return 0;
    }
}''', file = f)

#################################################################################################
#################################################################################################

if __name__ == '__main__':
  if len(sys.argv) < 4:
    print('G.py INPUT.json OUTPUT-DIR PACKAGE [PACKAGE WITH R.class]')
    sys.exit(1)

  packageR = sys.argv[3]
  if len(sys.argv) > 4:
    packageR = sys.argv[4]

  generate_G_java(sys.argv[1], sys.argv[2], sys.argv[3], packageR)
