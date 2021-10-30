'''
剑桥 释义和例句
'''
import sqlite3
import os.path
#open默认以只读方式打开文件，这里要传入一个'w'参数，告诉open是写模式
#写模式打开文件时，会立即覆盖原来的文件，原文件的数据全部删除
def openfile(word):
    fd = open('./7/'+word+'.txt',encoding = 'utf-8')
    return fd

#连接数据库，不存在则创建
conn = sqlite3.connect('youdao.db')
c = conn.cursor()
#创建表
c.execute('''CREATE TABLE IF NOT EXISTS NOTE
       (
        id            INTEGER       PRIMARY KEY  NOT NULL,
        English        VARCHAR(100)               NOT NULL,
        Chinese        VARCHAR(100)                NOT NULL);''')
c.execute('''CREATE TABLE IF NOT EXISTS JianQiao
       (
        English        VARCHAR(100) PRIMARY KEY    NOT NULL,
        Chinese        VARCHAR(100)                NOT NULL);''')
with open('./word.txt',encoding = 'utf-8') as f:
    a = f.readlines()
    for i in range(0,14325): 
        w =a[i]
        word = w.rstrip('\n') 
        #判断文件是否存在
        if (os.path.isfile('./7/'+word+'.txt')):
             fd = openfile(word)
             fd1 ="".join(fd.read())
             #将‘替换为“
             chinese = fd1.replace("'","''")
             #插入数据
             c.execute("INSERT OR IGNORE INTO JianQiao (English,Chinese) \
                       VALUES ('%s', '%s' )"%(word,chinese))
 
print("insert successfully")
conn.commit()
conn.close()