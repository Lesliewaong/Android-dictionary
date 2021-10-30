# -*- coding: utf-8 -*-
"""
删除重复行
"""

readDir = "./javaword.txt" 
writeDir = "./jw.txt" 
#生成一个集合
lines_seen = set() 
#打开写入文件
outfile=open(writeDir,"w") 
#打开读取文件
f = open(readDir,"r") 
#判断是否重复
for line in f: 
  if line not in lines_seen: 
    outfile.write(line) #将不存在的写入
    lines_seen.add(line) 
#关闭写入文件
outfile.close() 
print ("success")