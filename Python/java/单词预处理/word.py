
"""
编程单词（java）
生成纯单词txt
"""
import requests
from lxml import etree
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
def crawl(): 
    with open('./2.txt','w',encoding = 'utf-8') as h:  
        f = open('./java.txt',encoding = 'utf-8')#相对路径（相对于当前工作目录）
        a = f.readlines()
        #只读取单词表中的的英文字符
        for item in a:
            x = ''
            for i in item:
                #判断是否读取到非单词内容，如汉字，特殊字符
                if i == ':' or ('\u4e00' <= i <= '\u9fff') or i == '(' or i == '（':
                    break
                x += i
            x.lower()
            #解码 编码 删除字符串末尾的'\n' 
            vc = x.encode('utf-8').decode('utf-8-sig').rstrip('\n')
            if len(vc)==0:
                break
            #所有的元素合并为一个新的字符串
            w = "".join(vc)
            #检测字符串是否只由空白字符组成。
            if w.isspace():
                continue
            #个别单词中有/，过滤掉
            if '/' in w:
                continue
            #去除首尾空格 大写转小写
            word=w.strip().lower()
            url = 'http://www.youdao.com/w/'+word+'/#keyfrom=dict2.top'
            html =  get_page(url)
            # 构造 lxml.etree._Element 对象
            # lxml.etree._Element 对象还具有代码补全功能
            # 假如我们得到的 XML 文档不是规范的文档，该对象将会自动补全缺失的闭合标签
            html_elem = etree.HTML(html)
            #// 表示后代节点  * 表示所有节点  text() 表示文本节点
            # xpath 方法返回字符串或者匹配列表，匹配列表中的每一项都是 lxml.etree._Element 对象
            wd1 = html_elem.xpath('//*[@id="phrsListTab"]/h2/span/text()')
            wd = "".join(wd1)
            if (wd !=""):
                    h.write(wd+"\n")
                    h.flush()    
        f.close()#关闭文件   
    

if __name__ == '__main__':      
    crawl()