
"""
生成纯单词txt
"""

def crawl(): 
    with open('./word.txt','w',encoding = 'utf-8') as h:  
        f = open('./text1_utf8.txt',encoding = 'utf-8')#相对路径（相对于当前工作目录）
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
            print(word)           
            h.write(word+"\n")
            h.flush()
            
        f.close()#关闭文件   
    

if __name__ == '__main__':      
    crawl()