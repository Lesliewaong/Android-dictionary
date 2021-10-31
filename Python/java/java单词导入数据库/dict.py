'''
java单词导入数据库
'''
import sqlite3
import os.path



#连接数据库，不存在则创建
conn = sqlite3.connect('youdao.db')
c = conn.cursor()
#创建表
c.execute('''CREATE TABLE IF NOT EXISTS JAVA
       (
        ID             Integer      PRIMARY KEY    AUTOINCREMENT,
        English        VARCHAR(100)                NOT NULL,
        Chinese        VARCHAR(100)                NOT NULL,
        British        VARCHAR(100)                NOT NULL,
        American       VARCHAR(100)                NOT NULL);''')
with open('./jw.txt',encoding = 'utf-8') as f:
    a = f.readlines()
    for i in range(0,1181): 
        w =a[i]
        word = w.rstrip('\n') 
        chinese =""
        british =""
        american =""
        #判断文件是否存在
        if (os.path.isfile('./chinese/'+word+'.txt')):
             fd1 = open('./chinese/'+word+'.txt',encoding = 'utf-8')
             chinese ="".join(fd1.read())
             if (os.path.isfile('./british/'+word+'.txt')):
                 fd2 = open('./british/'+word+'.txt',encoding = 'utf-8')
                 british ="".join(fd2.read())
             if (os.path.isfile('./american/'+word+'.txt')):
                 fd3 = open('./american/'+word+'.txt',encoding = 'utf-8')
                 american ="".join(fd3.read())
             #插入数据
             c.execute("INSERT OR IGNORE INTO JAVA  (English,Chinese,British,American) \
                       VALUES ('%s', '%s' ,'%s','%s')"%(word,chinese,british,american))
print("insert successfully")
conn.commit()
conn.close()