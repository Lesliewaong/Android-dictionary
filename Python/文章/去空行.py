# -*- coding: utf-8 -*-

'''
python读取文件，将文件中的空白行去掉
'''
def delblankline(infile, outfile):
    
    infopen = open(infile, 'r',encoding="utf-8")#初始文件
    outfopen = open(outfile, 'w',encoding="utf-8")#处理文件
    lines = infopen.readlines()#按行读取
    for line in lines:
        if line.split():
            outfopen.writelines(line)#非空写入新文件
        else:
            outfopen.writelines("")
    infopen.close()
    outfopen.close()
for i in range(1,158) : 
    delblankline("./1/"+str(i)+".txt", "./2/"+str(i)+".txt")
