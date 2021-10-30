'''
有道词典语音爬虫
程序思想：
有两个本地语音库，美音库Speech_US，英音库Speech_US
调用有道api，获取语音MP3，存入对应的语音库中
使用多进程增加爬取速度
'''
from multiprocessing import Pool
import os
import socket
import urllib.request
from urllib import error
class youdao():
    def __init__(self, type=2, word='hellow'):
        '''
        调用youdao API
        type = 2：美音
        type = 1：英音

        判断当前目录下是否存在两个语音库的目录
        如果不存在，创建
        '''
        word = word.lower()  # 小写
        self._type = type  # 发音方式
        self._word = word  # 单词

        # 文件根目录 先得到绝对路径，再得到目录名称
        self._dirRoot = os.path.dirname(os.path.abspath(__file__))
        #拼接路径
        if 2 == self._type:
            self._dirSpeech = os.path.join(self._dirRoot, 'Speech_US')  # 美音库
        else:
            self._dirSpeech = os.path.join(self._dirRoot, 'Speech_EN')  # 英音库

        # 判断是否存在美音库
        if not os.path.exists('Speech_US'):
            # 不存在，就创建
            os.makedirs('Speech_US')
        # 判断是否存在英音库
        if not os.path.exists('Speech_EN'):
            # 不存在，就创建
            os.makedirs('Speech_EN')
    # 设置发音方式
    def setAccent(self, type):
        '''
        type = 2：美音
        type = 1：英音
        '''
        self._type = type 

        if 2 == self._type:
            self._dirSpeech = os.path.join(self._dirRoot, 'Speech_US')  # 美音库
        else:
            self._dirSpeech = os.path.join(self._dirRoot, 'Speech_EN')  # 英音库

    # def getAccent(self):
    #     '''
    #     type = 2：美音
    #     type = 1：英音
    #     '''
    #     return self._type

    def down(self, type,word):
        '''
        下载单词的MP3
        判断语音库中是否有对应的MP3
        如果没有就下载
        '''
        self.setAccent(type)
        word = word.lower()  # 小写
        tmp = self._getWordMp3FilePath(type,word)#判断文件是否存在
        if tmp is None:
            self._getURL()  # 组合URL
            # 调用下载程序，下载到目标文件夹
            # 下载到目标地址
            try: #使用try except方法进行各种异常处理
                response = urllib.request.urlopen(self._url, timeout=5)
                data = response.read()
                with open(self._filePath,'ab') as audio:
                    audio.write(data)
                    audio.flush()
                print('%s.mp3 下载完成' % self._word)
            except error.HTTPError as err: 
                print('HTTPerror, code: %s' % err.code)
            except error.URLError as err:
                print('URLerror, reason: %s' % err.reason)
            except socket.timeout:
                print('Time Out!')
            except:
                print('Unkown Error!')
  
        else:
            print('已经存在 %s.mp3, 不需要下载' % self._word)

        # 返回声音文件路径
        return self._filePath

    def _getURL(self):
        '''
        私有函数，生成发音的目标URL
        http://dict.youdao.com/dictvoice?type=2&audio=
        '''
        self._url = r'http://dict.youdao.com/dictvoice?type=' + str(self._type) + r'&audio=' + self._word.replace(" ", "%20")

    def _getWordMp3FilePath(self, type,word):
        '''
        获取单词的MP3本地文件路径
        如果有MP3文件，返回路径(绝对路径)
        如果没有，返回None
        '''
        word = word.lower()  # 小写
        self._word = word
        self._fileName = self._word +'.mp3'#分别命名
        self._filePath = os.path.join(self._dirSpeech, self._fileName)

        # 判断是否存在这个MP3文件
        if os.path.exists(self._filePath):
            # 存在这个mp3
            return self._filePath
        else:
            # 不存在这个MP3，返回none
            return None
def audio(i):
    f = open('./javaword.txt',encoding = 'utf-8')#相对路径（相对于当前工作目录）
    a = f.readlines()  
    c =a[i]
    word = c.rstrip('\n')
    
    sp = youdao()
    sp.down(2,word)
    sp.down(1,word)
if __name__ == '__main__':
    pool=Pool(processes=4)#申请的进程数
    for i in range(0,1340):   
        pool.apply_async(audio,(i,)) #异步执行，非阻塞方式    
    pool.close()#关闭进程池，关闭之后，不能再向进程池中添加进程
    pool.join()#当进程池中的所有进程执行完后，主进程才可以继续执行。
    
