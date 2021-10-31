# -*- coding: utf-8 -*-
"""
XPath和BeautifulSoup爬取可可英语双语文章
"""
import requests
from lxml import etree
# 使用文档解析类库
from bs4 import BeautifulSoup
# 使用网络请求类库
import urllib.request
def get_page(url):
    #构造请求头部
    headers = {
          'User-Agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36'
    }
    #向目标网址发送请求，接收响应，返回一个 Response 对象
    response = requests.get(url=url,headers=headers)
    response.encoding="utf-8"
    requests.adapters.DEFAULT_RETRIES = 5
    # 获得网页源代码
    html = response.text
    return html
def crawl(url,i):
    html =  get_page(url)
    # 构造 lxml.etree._Element 对象
    # lxml.etree._Element 对象还具有代码补全功能
    # 假如我们得到的 XML 文档不是规范的文档，该对象将会自动补全缺失的闭合标签
    html_elem = etree.HTML(html)
    #// 表示后代节点  * 表示所有节点  text() 表示文本节点
    # xpath 方法返回字符串或者匹配列表，匹配列表中的每一项都是 lxml.etree._Element 对象
    fy1 = html_elem.xpath('//*[@id="article"]//*/text()')
    #每个元素后面加换行符
    for x in range(len(fy1)):
        fy1[x]+='\n'
    chinese = "".join(fy1).replace("'","''")#数据库插入时'会报错
    with open('./1/'+str(i)+'.txt','w',encoding = 'utf-8') as fd:
        fd.write(chinese+'\n')#写到缓存区
        fd.flush()#将缓存区数据写入文件 
if __name__ == '__main__':
    i=1
    # 输入网址
    for x in range(1,12):
        html_doc = "http://www.kekenet.com/Article/17371/List_"+str(x)+".shtml"
        # 获取请求
        req = urllib.request.Request(html_doc)
        # 打开页面
        webpage = urllib.request.urlopen(req)
        # 读取页面内容
        html = webpage.read()
        # 解析成文档对象
        soup = BeautifulSoup(html, 'html.parser')   #文档对象
        #查找文档中所有a标签
        for k in soup.find_all('a'):
            #print(k)
            #查找href标签
            link=k.get('href')
            #筛选链接
            if(link is not None and "http://www.kekenet.com/Article/2" in link):
                crawl(link,i)
                i=i+1
          
           
        
