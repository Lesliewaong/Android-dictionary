# -*- coding: utf-8 -*-
"""
缩略词爬取
"""
import sqlite3

import requests
from lxml import etree
#连接数据库，不存在则创建
conn = sqlite3.connect('youdao.db')
c = conn.cursor()
#创建表
c.execute('''CREATE TABLE IF NOT EXISTS SLC
       (
       
        English        VARCHAR(100) PRIMARY KEY    NOT NULL,
        Chinese       VARCHAR(100)                NOT NULL);''')
def get_page(url):
    #构造请求头部
    headers = {
        'User-Agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36'
    }
    #向目标网址发送请求，接收响应，返回一个 Response 对象
    response = requests.get(url=url,headers=headers)
    requests.adapters.DEFAULT_RETRIES = 5
    # 获得网页源代码
    html = response.text
    return html
#ord()函数主要用来返回对应字符的ascii码，chr()主要用来表示ascii码对应的字符他的输入时数字，可以用十进制，也可以用十六进制。
for i in range(ord("A"),ord("Z")+1):
    for j in range(1,5):
        #打开单词文件读取单词查询
        url = 'http://abbr.dict.cn/list/'+chr(i)+'/'+str(j)
        html =  get_page(url)
        # 构造 lxml.etree._Element 对象
        # lxml.etree._Element 对象还具有代码补全功能
        # 假如我们得到的 XML 文档不是规范的文档，该对象将会自动补全缺失的闭合标签
        html_elem = etree.HTML(html)
        #// 表示后代节点  * 表示所有节点  text() 表示文本节点
        # xpath 方法返回字符串或者匹配列表，匹配列表中的每一项都是 lxml.etree._Element 对象
        fy1 = html_elem.xpath('//*[@id="main"]/div[3]/div[2]/div[1]/div[1]/div[2]/ul//*/text()')
       
        #每个元素后面加换行符
        #for x in range(3,len(fy1),4):
         #   fy1[x]+='\n'
        for y in range(0,len(fy1),4):
            english="".join(fy1[y]).replace("/","")
            chinese = "".join(fy1[y]+fy1[y+1]+fy1[y+2]+fy1[y+3]).replace("'","''")#数据库插入时'会报错
            #with open('./ssc/'+english+'.txt','w',encoding = 'utf-8') as fd:
             #   fd.write(chinese+'\n')#写到缓存区
             #   fd.flush()#将缓存区数据写入文件 
              #插入数据
            c.execute("INSERT OR IGNORE INTO SLC  (English,Chinese) \
                       VALUES ('%s', '%s')"%(english,chinese))

print("insert successfully")
conn.commit()
conn.close()
