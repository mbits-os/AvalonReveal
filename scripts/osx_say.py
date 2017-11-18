#!/usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import print_function

import json
import sys
import subprocess
import os.path

dname = os.path.dirname(sys.argv[1])
lang = os.path.basename(dname)

languages = {
	'pl': ['Zosia', 'pol', 'wersja polska', {
		'mask': ' - ',
		'titles' : {
			'all': 'Wszyscy',
			'merlin': 'Merlin',
			'lancelot': 'Lancelot',
			'minions': 'Poplecznicy Mordreda'
		},
		'actions' : {
			'warning': 'informacje dodatkowe',
			'wake-up': 'otwarcie oczu',
			'wake-up2': 'otwarcie oczu (Morgana)',
			'sleep': u'zamknięcie oczu',
			'reveal': 'podniesienie kciuka',
			'no-reveal': u'ukrycie się przed Merlinem',
			'hide': 'ukrycie kciuka',
		},
		'fixups' : {
			'all:warning': 'Informacje dodatkowe'
		},
		'speech' : [
			[ 'Parsifal', 'Pars-ifal' ],
			[ u'Zły', 'Zuy' ],
			[ u'Zła', 'Zua' ]
		]
	}],
	'en': ['Susan', 'eng', 'English Version', {
		'mask': ' ',
		'titles' : {
			'all': 'Everybody',
			'merlin': 'Merlin',
			'lancelot': 'Lancelot',
			'minions': 'Minions of Mordred'
		},
		'actions' : {
			'warning': 'additional info',
			'wake-up': 'opening eyes',
			'wake-up2': 'opening eyes (Morgana)',
			'sleep': 'closing eyes',
			'reveal': 'raising the thumb',
			'no-reveal': 'hiding from Merlin',
			'hide': 'hiding thumb',
		},
		'fixups' : {
			'all:warning': 'Additional info'
		}
	}],
}

voice, iso639_2, subtitle, title = languages[lang]
data = {}
speech = title['speech'] if 'speech' in title else []

def make_title(actor, action):
	ident = '{}:{}'.format(actor, action)
	if 'fixups' in title and ident in title['fixups']:
		return title['fixups'][ident]
	if 'titles' in data and actor in data['titles']:
		actor = data['titles'][actor]
		if not isinstance(actor, basestring): actor = actor[0]
	elif 'titles' in title and actor in title['titles']:
		actor = title['titles'][actor]
		if not isinstance(actor, basestring): actor = actor[0]

	if 'actions' in title and action in title['actions']:
		action = title['actions'][action]
		mask = ': ' if not 'mask' in title else title['mask']
		result = actor + mask + action
		return result

	print(ident)
	return ident

with open(sys.argv[1]) as f:
	text = f.read()
	data = json.loads(text)

commands = []
if 'dialogs' in data:
	track = 1
	for key in data['dialogs']:
		dloc = os.path.join(dname, 'media')
		commands.append(['mkdir', '-p', dloc])
		dlg = data['dialogs'][key]
		for d in dlg:
			msg = dlg[d] #.replace('Parsifal', 'Pars-ifal')
			for src, dst in speech:
				msg = msg.replace(src, dst) 
			out = os.path.join(dloc, key.replace('/','-') + '_' + d.replace('/','-'))
			commands.append(['say', '-v', voice, '-o', out + '.mp4', msg + '.'])
			commands.append([
				'ffmpeg', '-y', '-i', out + '.mp4',
				# 'ffmpeg', '-y', '-loglevel', 'panic', '-i', out + '.mp4',
				'-metadata', u'title=' + make_title(key, d),
				'-metadata', u'album=The Resistance: Avalon ({})'.format(subtitle),
				'-metadata', 'TPE1=midnightBITS',
				'-metadata', 'genre=101', # Speech
				'-metadata', u'COMM={}'.format(dlg[d]),
				'-metadata', 'TLAN={}'.format(iso639_2),
				'-metadata', 'track={}'.format(track),
				out + '.mp3'
			])
			commands.append(['rm', out + '.mp4'])
			track += 1

for command in commands:
	subprocess.call(command)
	sys.stderr.write('.')
sys.stderr.write('\n')
