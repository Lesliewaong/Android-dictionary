"""
有道词典 释义、音标、例句爬取
sqlite数据库不支持多进程
"""
import requests
from lxml import etree
import sqlite3
def word(z):    
    #连接数据库，不存在则创建
    conn = sqlite3.connect('youdao.db')
    c = conn.cursor()
    
    #创建表(若存在则不创建)
    c.execute('''CREATE TABLE IF NOT EXISTS DICT
           (
            English        VARCHAR(100) PRIMARY KEY    NOT NULL,
            Chinese        VARCHAR(100)                NOT NULL,
            British        VARCHAR(100)                NOT NULL,
            American       VARCHAR(100)                NOT NULL,
            Sentence       VARCHAR(100)                NOT NULL);''')
    
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
    #打开单词文件读取单词查询
    with open('./word.txt',encoding = 'utf-8') as f:
        a = f.readlines()
        for i in range(z-1,z):
            w =a[i]
            word = w.rstrip('\n') 
            url = 'http://www.youdao.com/w/'+word+'/#keyfrom=dict2.top'
            html =  get_page(url)
            
            # 构造 lxml.etree._Element 对象
            # lxml.etree._Element 对象还具有代码补全功能
            # 假如我们得到的 XML 文档不是规范的文档，该对象将会自动补全缺失的闭合标签
            html_elem = etree.HTML(html)
            
            #// 表示后代节点  * 表示所有节点  text() 表示文本节点
            # xpath 方法返回字符串或者匹配列表，匹配列表中的每一项都是 lxml.etree._Element 对象
            fy1 = html_elem.xpath('//*[@id="phrsListTab"]/div[2]/ul//*/text()')
            #每个元素后面加换行符
            for x in range(len(fy1)):
                fy1[x]+='\n'
            chinese = "".join(fy1).replace("'","''")#数据库插入时'会报错
            
            fy2 = html_elem.xpath('//*[@id="phrsListTab"]/h2/div/span[1]//*/text()') 
            british = "".join(fy2).replace("'","''")
            
            fy3 = html_elem.xpath('//*[@id="phrsListTab"]/h2/div/span[2]//*/text()') 
            american = "".join(fy3).replace("'","''")
         
            #爬例句
            n=1
            sentence=''
            while True:
                fy4 = html_elem.xpath('//*[@id="bilingual"]/ul/li['+str(n)+']/p[1]//text()') 
                fy5 = html_elem.xpath('//*[@id="bilingual"]/ul/li['+str(n)+']/p[2]//text()') 
                #整齐
                s = "".join(fy4).replace("'","''").strip()+'\n'+"".join(fy5).replace("'","''").strip()
                s1 =s.replace( ' ' , '' )
                s2 =str(n)+'.'+s  #加编号
                #判断是否为空
                if (s1.strip()==''):
                    break
                #所有例句整合
                sentence =sentence+s2+'\n'
                n=n+1
              
            if (chinese !=""):
                print(i)
                #插入数据，若存在则不插入
                c.execute("INSERT OR IGNORE INTO DICT (English,Chinese,British,American,Sentence) \
                               VALUES ('%s', '%s' ,'%s','%s','%s')"%(word,chinese,british,american,sentence))
                
                   
    conn.commit()
    conn.close()
if __name__ == "__main__":
    z=73696
    while ( z <=74399):        
        word(z)
        z=z+1