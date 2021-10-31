# -*- coding: utf-8 -*-
"""
编程单词有道爬取
爬虫速度改进：
多进程先存到txt

"""
from multiprocessing import Pool
import requests
from lxml import etree


def word(i): 
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
    with open('./javaword.txt',encoding = 'utf-8') as f:
        a = f.readlines()      
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
        if (chinese !=""):
            with open('./chinese/'+word+'.txt','w',encoding = 'utf-8') as fd:
                fd.write(chinese+'\n')#写到缓存区
                fd.flush()#将缓存区数据写入文件 
        else:           
            wy1 = html_elem.xpath('//*[@id="tWebTrans"]/div[1]/p[1]//*/text()')
            wy = "".join(wy1).replace("'","''")#数据库插入时'会报错
            if (wy !=""):
                with open('./chinese/'+word+'.txt','w',encoding = 'utf-8') as fd:
                    fd.write(wy+'\n')#写到缓存区
                    fd.flush()#将缓存区数据写入文件
        fy2 = html_elem.xpath('//*[@id="phrsListTab"]/h2/div/span[1]//*/text()') 
        british = "".join(fy2).replace("'","''")
        if (british !=""):
            with open('./british/'+word+'.txt','w',encoding = 'utf-8') as fd:
                fd.write(british+'\n')#写到缓存区
                fd.flush()#将缓存区数据写入文件 
        fy3 = html_elem.xpath('//*[@id="phrsListTab"]/h2/div/span[2]//*/text()') 
        american = "".join(fy3).replace("'","''")
        if (american !=""):
            with open('./american/'+word+'.txt','w',encoding = 'utf-8') as fd:
                fd.write(american+'\n')#写到缓存区
                fd.flush()#将缓存区数据写入文件 
       
if __name__ == '__main__':
    pool=Pool(processes=4)#申请的进程数
    for i in range(0,1340):        
        pool.apply_async(word,(i,)) #异步执行，非阻塞方式    
    pool.close()#关闭进程池，关闭之后，不能再向进程池中添加进程
    pool.join()#当进程池中的所有进程执行完后，主进程才可以继续执行。
