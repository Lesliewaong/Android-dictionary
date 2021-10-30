for i in range(1,158) :   
    with open("./2/"+str(i)+".txt", 'r',encoding="utf-8") as f:
        lines = f.readlines()#按行读取
        lines = lines[:-4]#舍掉后四行
    with open("./2/"+str(i)+".txt", 'w',encoding="utf-8") as f:
        f.write(''.join(lines))#重新写入