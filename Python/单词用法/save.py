'''
单词用法导入数据库
'''
import sqlite3
import os.path

#连接数据库，不存在则创建
conn = sqlite3.connect('youdao.db')
# 获取游标对象用来操作数据库
c = conn.cursor()
#创建表
c.execute('''CREATE TABLE IF NOT EXISTS USE
       (
       
        English        VARCHAR(100) PRIMARY KEY    NOT NULL,
        Synonyms       VARCHAR(100)                NOT NULL,
        WordGroup        VARCHAR(100)                NOT NULL,
        Discriminate      VARCHAR(100)                NOT NULL);''')
with open('./word.txt',encoding = 'utf-8') as f:
    a = f.readlines()
    for i in range(0,74398): 
        w =a[i]
        word = w.rstrip('\n') 
        synonyms =""
        wordGroup =""
        discriminate =""
        sign = False
        #判断文件是否存在
        if (os.path.isfile('./synonyms/'+word+'.txt')):
             fd1 = open('./synonyms/'+word+'.txt',encoding = 'utf-8')
             synonyms ="".join(fd1.read())
             sign = True
        if (os.path.isfile('./wordGroup/'+word+'.txt')):
             fd2 = open('./wordGroup/'+word+'.txt',encoding = 'utf-8')
             wordGroup ="".join(fd2.read())
             sign = True
        if (os.path.isfile('./discriminate/'+word+'.txt')):
             fd3 = open('./discriminate/'+word+'.txt',encoding = 'utf-8')
             discriminate ="".join(fd3.read())
             sign = True
        if(sign):
            #插入数据
            c.execute("INSERT OR IGNORE INTO USE  (English,Synonyms,WordGroup,Discriminate) \
                       VALUES ('%s', '%s' ,'%s','%s')"%(word,synonyms,wordGroup,discriminate))
print("insert successfully")
c.close()#关闭游标,不关似乎不会有明显问题
conn.commit()# 提交事务
conn.close()# 关闭链接