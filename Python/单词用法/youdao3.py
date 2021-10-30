# -*- coding: utf-8 -*-
"""
单词用法爬取
同义词，词组短语，词组辨析
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
    with open('./word.txt',encoding = 'utf-8') as f:
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
        #同义词
        n=1
        synonyms=''
        while True:
            fy1 = html_elem.xpath('//*[@id="synonyms"]/ul/li['+str(n)+']//text()') 
            fy2 = html_elem.xpath('//*[@id="synonyms"]/ul/p['+str(n)+']//text()') 
            #整齐
            s = "".join(fy2).replace("'","''").replace('\n', '').replace('\r', '')
            s1="".join(fy1).replace("'","''").strip()+'\n'+' '.join(s.split())#替换多个空格
            #判断是否为空
            if (s1.strip()==''):
                break
            #整合
            synonyms =synonyms+s1+'\n'
            n=n+1
        if (synonyms !=""):    
            with open('./synonyms/'+word+'.txt','w',encoding = 'utf-8') as fd:   
                fd.write(synonyms+'\n')#写到缓存区
                fd.flush()#将缓存区数据写入文件 
        #词组短语
        m=1
        wordGroup=''
        while True:
            fy3 = html_elem.xpath('//*[@id="wordGroup"]/p['+str(m)+']//text()') 
            #整齐
            w = "".join(fy3).replace("'","''").replace('\n', '').replace('\r', '')
            w1=' '.join(w.split())#替换多个空格
            #判断是否为空
            if (w1.strip()==''):
                break
            #整合
            wordGroup =wordGroup+w1+'\n'
            m=m+1
        if (wordGroup !=""):    
            with open('./wordGroup/'+word+'.txt','w',encoding = 'utf-8') as fd:   
                fd.write(wordGroup+'\n')#写到缓存区
                fd.flush()#将缓存区数据写入文件 
  
       #词语辨析
        l=1
        discriminate=''
        while True:
            fy4 = html_elem.xpath('//*[@id="discriminate"]/div[1]/div[2]/div['+str(l)+']//text()') 
            #整齐
            d = "".join(fy4).replace("'","''").replace('\n', '').replace('\r', '')
            d1=' '.join(d.split())#替换多个空格
            #判断是否为空
            if (d1.strip()==''):
                break
            #整合
            discriminate =discriminate+d1+'\n'
            l=l+1
        if (discriminate !=""):    
            with open('./discriminate/'+word+'.txt','w',encoding = 'utf-8') as fd:   
                fd.write(discriminate+'\n')#写到缓存区
                fd.flush()#将缓存区数据写入文件        
       
if __name__ == '__main__':
    pool=Pool(processes=10)#申请的进程数
    for i in range(100,74398):        
        pool.apply_async(word,(i,)) #异步执行，非阻塞方式    
    pool.close()#关闭进程池，关闭之后，不能再向进程池中添加进程
    pool.join()#当进程池中的所有进程执行完后，主进程才可以继续执行。

