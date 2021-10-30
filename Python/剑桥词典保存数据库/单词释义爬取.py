#单词释义爬取
import requests
from lxml import etree
import os
# 获取网页源代码
def get_page(url):
    headers = {
        'USER-AGENT':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36'
    }
    response = requests.get(url=url,headers=headers)
    requests.adapters.DEFAULT_RETRIES = 5
    html = response.text
    return html
#打开文件
def openfile(word):
    fd = open('./FileOperation/1/'+word+'.txt','w',encoding = 'utf-8')
    #open默认以只读方式打开文件，这里要传入一个'w'参数，告诉open是写模式
    #写模式打开文件时，会立即覆盖原来的文件，原文件的数据全部删除
    return fd
#保存文件
def save2file(fw,a):
    fw.write(a+'\n')#写到缓存区
    fw.flush()#将缓存区数据写入文件   
# 解析网页源代码
def parse_page(word,fd,html):
    html_elem = etree.HTML(html)
    #利用循环爬取所需位置的内容
    l =1
    while True:
        
        pd1 =html_elem.xpath('//*[@id="english-chinese-simplified-1-'+str(l)+'-1"]/div/div[1]/p/span/span/text()')
        pd2 =html_elem.xpath('//*[@id="english-chinese-simplified-1-'+str(l)+'-1"]/div/div[1]/div/div[1]/p/span/span/text()')
        if len(" ".join(pd1))==0 and len(" ".join(pd2))==0:                  
            break
        j =1 
        while True:
            k = 1
            
            panduan1 =html_elem.xpath('//*[@id="english-chinese-simplified-1-'+str(l)+'-'+str(j)+'"]/div/div[1]/p/span/span/text()')
            panduan2 =html_elem.xpath('//*[@id="english-chinese-simplified-1-'+str(l)+'-'+str(j)+'"]/div/div[1]/div/div/p/span/span/text()')
            if len(" ".join(panduan1))==0 and len(" ".join(panduan2))==0:
                  
                    break   
            while True:
                x1 = html_elem.xpath('//*[@id="english-chinese-simplified-1-'+str(l)+'-'+str(j)+'"]/div/div['+str(k)+']/p//*/text()')
                                                
                if len("".join(x1))==0:             
                    break
                if '›' not in "".join(x1) : 
                    
                    print("".join(x1))
                    save2file(fd,"".join(x1))
                    fy = html_elem.xpath('//*[@id="english-chinese-simplified-1-'+str(l)+'-'+str(j)+'"]/div/div['+str(k)+']/span/span/text()')
                    print("".join(fy))
                    save2file(fd,"".join(fy))
                    L =[]
                    i = 0
                    while True:  
                        a = html_elem.xpath('//*[@id="english-chinese-simplified-1-'+str(l)+'-'+str(j)+'"]/div/div['+str(k)+']/span/div['+str(i+1)+']//*/text()')
                        L.append(a) 
                        i += 1
                        if len(a)==0:
                            break
                    for _ in L:
                        print("".join(_)) 
                        save2file(fd,"".join(_))
                       
                k += 1                
            k =1
            while True:
                x1 = html_elem.xpath('//*[@id="english-chinese-simplified-1-'+str(l)+'-'+str(j)+'"]/div/div/div/div['+str(k)+']/p//*/text()')
                if len("".join(x1))==0:
                    break
                if '›' not in "".join(x1): 
                    print("".join(x1))
                    save2file(fd,"".join(x1))
                    fy = html_elem.xpath('//*[@id="english-chinese-simplified-1-'+str(l)+'-'+str(j)+'"]/div/div/div/div['+str(k)+']/span/span/text()')
                                        
                    print("".join(fy))
                    save2file(fd,"".join(fy))
                    L =[]
                    i = 0
                    while True:  
                        a = html_elem.xpath('//*[@id="english-chinese-simplified-1-'+str(l)+'-'+str(j)+'"]/div/div/div/div['+str(k)+']/span/div['+str(i+1)+']//*/text()')
                        L.append(a) 
                        i += 1
                        if len(a)==0:
                            break
                    for _ in L:
                        print("".join(_)) 
                        save2file(fd,"".join(_))
                        
                k += 1 
           
            j+=1
        l +=1
# 开始爬取网页
def crawl(): 
    f = open('./FileOperation/text1_utf8.txt',encoding = 'utf-8')#相对路径（相对于当前工作目录）
    a = f.readlines()
    #只读取单词表中的的英文字符
    for item in a:
        x = ''
        for i in item:
            if i == ' ':
                break
            x += i
        x.lower()
        vc = x.encode('utf-8').decode('utf-8-sig').rstrip('\n').split('、')
        if len(vc)==0:
            break
        word = "".join(vc)
        #个别单词中有/，过滤掉
        if '/' in word:
            continue
        fd = openfile(word)
        #遍历所有单词的url
        url = 'https://dictionary.cambridge.org/dictionary/english-chinese-simplified/'+word
        html = get_page(url)
        parse_page(word,fd,html)
        fd.close()#关闭文件时会将缓存区的数据写入文件
        #如果没有爬取到内容，将此文件删除
        if os.path.getsize('./FileOperation/1/'+word+'.txt')==0:
            os.remove('./FileOperation/1/'+word+'.txt')
    f.close()#关闭文件   
    print('结束爬取')

if __name__ == '__main__':
    crawl()
   


    
    
    
    
 
    
    