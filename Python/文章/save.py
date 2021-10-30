'''
java单词导入数据库
'''
import sqlite3
import os.path



#连接数据库，不存在则创建
conn = sqlite3.connect('youdao.db')
c = conn.cursor()
#创建表
c.execute('''CREATE TABLE IF NOT EXISTS ARTICLE
       (
        ID             Integer      PRIMARY KEY    AUTOINCREMENT,
        English        VARCHAR(100)                NOT NULL);''')
for i in range(1,158):
    with open('./2/'+str(i)+'.txt',encoding = 'utf-8') as fd:
        english ="".join(fd.read())
        #插入数据
        c.execute("INSERT OR IGNORE INTO ARTICLE  (English) \
                  VALUES ('%s')"%(english))
print("insert successfully")
conn.commit()
conn.close()