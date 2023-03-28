#
#  Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
#
#  File name: run-dev.py
#  Last modified: 11/03/2023, 12:43
#  Project name: jwizard-discord-bot
#
#  Licensed under the MIT license; you may not use this file except in compliance with the License.
#
#  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
#  documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
#  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
#  permit persons to whom the Software is furnished to do so, subject to the following conditions:
#
#  THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL COPIES OR
#  SUBSTANTIAL PORTIONS OF THE SOFTWARE.
#
#  The software is provided "as is", without warranty of any kind, express or implied, including but not limited
#  to the warranties of merchantability, fitness for a particular purpose and noninfringement. In no event
#  shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an
#  action of contract, tort or otherwise, arising from, out of or in connection with the software or the use
#  or other dealings in the software.
#

import os
import re
import sys
import subprocess

start_java_heap_size    = '256m' # -Xms parameter, min. 128MB, recommended 256MB
max_java_heap_size      = '512m' # -Xmx parameter

executable_jar_file_name = ''
default_jar_file_pattern = 'jwizard-discord-bot-\\d.\\d.\\d.jar'
sys.argv.pop()

if len(sys.argv) > 1:
    print('[python run script err] <> Available only argument: --execJar=<nameOfJarFile>')
    exit(1)

if len(sys.argv) == 1:
    key, value = sys.argv[0].split('=')
    if key == '--execJar':
        executable_jar_file_pattern = value
    else:
        executable_jar_file_pattern = default_jar_file_pattern
else:
    executable_jar_file_pattern = default_jar_file_pattern

jre_version = subprocess.check_output(['java', '-version'], stderr=subprocess.STDOUT).decode()
jre_version = re.search('\"(\\d+\\.\\d+).*\"', jre_version).groups()[0]

jre_version = jre_version.split('.')[0]
if not jre_version == '17':
    print('[python run script err] <> To run application you must have installed JRE 17.X')
    exit(2)

files = [f for f in os.listdir('.') if os.path.isfile(f)]
executable_exist = False

for f in files:
    if re.search(executable_jar_file_pattern, f):
        executable_exist = True
        executable_jar_file_name = f

configuration_exist = [f for f in files if f == 'properties-dev.yml']
env_exist = [f for f in files if f == '.env']

if not executable_exist:
    print('[python run script err] <> Executable JAR file not found in current directory')
    exit(3)

if not configuration_exist:
    print('[python run script err] <> Configuration file properties-dev.yml not found in current directory')
    print('[python run script err] <> Download file from:')
    print('[python run script err] <> https://github.com/Milosz08/JWizard_Discord_Bot/blob/master/properties-dev.yml')
    exit(4)

if not env_exist:
    print('[python run script err] <> Env file not found in current directory')
    exit(5)

executable_script = \
    f'java ' \
    f'-Xmx{max_java_heap_size} -Xms{start_java_heap_size} ' \
    f'-Duser.timezone=UTC ' \
    f'-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/ ' \
    f'-jar {executable_jar_file_name} ' \
    f'--mode=dev'

print('[python run script info] <> Executing JWizard bot JAR file in development mode...')
print(f'[python run script info] <> {executable_script}')

os.system(executable_script)
