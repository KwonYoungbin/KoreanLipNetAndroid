# -*- coding: utf-8 -*- 

import os, fnmatch, sys, errno  

SOURCE_PATH = sys.argv[1] # evaluation/samples/GRID
SOURCE_EXTS = sys.argv[2] # *.align
TARGET_PATH = sys.argv[3] # TARGET/


"""
사용법
python align.py ./ch1/align/ *.align ./
"""
def mkdir_p(path):
    try:
        os.makedirs(path)
    except OSError as exc:  # Python >2.5
        if exc.errno == errno.EEXIST and os.path.isdir(path):
            pass
        else:
            raise

def find_files(directory, pattern):
    for root, dirs, files in os.walk(directory):
        for basename in files:
            if fnmatch.fnmatch(basename, pattern):
                filename = os.path.join(root, basename)
                yield filename


for filepath in find_files(SOURCE_PATH, SOURCE_EXTS):
    # print find_files(SOURCE_PATH, SOURCE_EXTS)
    print "Processing: {}".format(filepath)
    i=0
    j=0
    f = open(filepath, 'r')
    lines = f.readlines()
    f2 = open(filepath, 'w')
    filepath_wo_ext = os.path.splitext(filepath)[0]
    print filepath_wo_ext
    target_dir = os.path.join(TARGET_PATH, filepath_wo_ext)
    # print target_dir

    # 파일 내용 쓰기
    for i in range(len(lines)):
        # print("@@@@@@@@@@@@@@@@@@@@@@@")
        # print str(lines[i])
        # print("=============")
        a = lines[i].split(" ")
        # print a
        # print a[0]
        # print("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

        for j in range(2):
            k = 0
            if int(a[j]) != 0:
                k = int(a[j]) + 75000
            a[j] = k
            if j==0:
                f2.writelines(str(k) + " ")
            else:
                f2.writelines(str(k) + " " + a[2])
        

f.close()
f2.close()
