#
#  Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
#
#  File name: run-dev.py
#  Last modified: 01/03/2023, 11:31
#  Project name: jwizard-discord-bot
#
#  Licensed under the MIT license; you may not use this file except in compliance with the License.
#
#  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
#  documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
#  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
#  permit persons to whom the Software is furnished to do so, subject to the following conditions:
#
#  THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL
#  COPIES OR SUBSTANTIAL PORTIONS OF THE SOFTWARE.
#

import os
import re
import sys
import subprocess

startJavaHeapSize = '256m'      # -Xms parameter, min. 128MB
maxJavaHeapSize = '512m'        # -Xmx parameter

executableJarFileName = ''
defaultJarFilePattern = 'jwizard-discord-bot-\\d.\\d.\\d.jar'
sys.argv.pop()

if len(sys.argv) > 1:
    print('[python run script err] <> Available only argument: --execJar=<nameOfJarFile>')
    exit(1)

if len(sys.argv) == 1:
    key, value = sys.argv[0].split('=')
    if key == '--execJar':
        executableJarFilePattern = value
    else:
        executableJarFilePattern = defaultJarFilePattern
else:
    executableJarFilePattern = defaultJarFilePattern

jreVersion = subprocess.check_output(['java', '-version'], stderr=subprocess.STDOUT).decode()
jreVersion = re.search('\"(\\d+\\.\\d+).*\"', jreVersion).groups()[0]

jreVersion = jreVersion.split('.')[0]
if not jreVersion == '17':
    print('[python run script err] <> To run application you must have installed JRE 17.X')
    exit(2)

files = [f for f in os.listdir('.') if os.path.isfile(f)]
executableExist = False

for f in files:
    if re.search(executableJarFilePattern, f):
        executableExist = True
        executableJarFileName = f

configurationExist = [f for f in files if f == 'properties-dev.yml']
envExist = [f for f in files if f == '.env']

if not executableExist:
    print('[python run script err] <> Executable JAR file not found in current directory')
    exit(3)

if not configurationExist:
    print('[python run script err] <> Configuration file properties-dev.yml not found in current directory')
    print('[python run script err] <> Download file from:')
    print('[python run script err] <> https://github.com/Milosz08/JWizard_Discord_Bot/blob/master/properties-dev.yml')
    exit(4)

if not envExist:
    print('[python run script err] <> Env file not found in current directory')
    exit(5)

executableScript = \
    f'java ' \
    f'-Xmx{maxJavaHeapSize} -Xms{startJavaHeapSize} ' \
    f'-Duser.timezone=UTC ' \
    f'-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/ ' \
    f'-jar {executableJarFileName} ' \
    f'--mode=dev'

print('[python run script info] <> Executing JWizard bot JAR file in development mode...')
print(f'[python run script info] <> {executableScript}')

os.system(executableScript)
