
# coding: utf-8

# In[260]:


import bs4
from urllib.request import urlopen as uReq
from bs4 import BeautifulSoup as soup
import re
import pandas as pd
import datetime
import calendar
import time
import numpy as np
import time
import nltk
from nltk.corpus import stopwords
from collections import Counter
import json
import ast
import math


# In[123]:


nltk.download('stopwords')


# In[124]:


#COMPLETE TAG EXTRACTOR
tagSet=set()
for pageNo in range(1,94):  #No. of pages 1 to 93
  my_url='https://askubuntu.com/tags?page='+str(pageNo)+'&tab=name'
  uClient=uReq(my_url)
  page_html=uClient.read()
  uClient.close()
  page_soup=soup(page_html,"html.parser")
  question_summary=page_soup.findAll("a",{"class":"post-tag"})
 
  for tagNoInPage in range (len(question_summary)):
    tag=re.findall(r'>.*</a>',str(question_summary[tagNoInPage]))
    temp=str(tag[0]).replace(">","").replace("</a","")
    #print(temp)
    tagSet.add(temp)
  
  


# In[146]:


for i in (tagSet):  #was 3276
    print(i)


# In[126]:


df=pd.read_csv('questionsV7.csv',encoding = "ISO-8859-1")


# In[127]:


df=df.iloc[0:len(df),1:8]
df.columns


# In[139]:


df.iloc[0:20,:]


# In[128]:


#SYNONYM EXTRACTOR
tagSynonymDict={}
for pageNo in range(1,5):  #No. of pages
    synonymUrl='https://askubuntu.com/tags/synonyms?page='+str(pageNo)+'&tab=newest&dir=descending&filter=active'
    uClient=uReq(synonymUrl)
    page_html=uClient.read()
    uClient.close()
    page_soup=soup(page_html,"html.parser")
    tag_synonym=page_soup.findAll("a",{"class":"post-tag"})
    #No of tags are 32
    #no of pairs are 32/2 ie 16 
    for i in range (int(len(tag_synonym)/2)):  #32 iterations
        tagSynonymDict[re.findall(r'>.*</a>',str([tag_synonym[i*2]]))[0].replace('>',"").replace('</a',"")]=re.findall(r'>.*</a>',str([tag_synonym[(i*2)+1]]))[0].replace('>',"").replace('</a',"")
    


# In[129]:


(tagSynonymDict)


# In[130]:


#tag count per answer
answerTagDict={}
for question in range(0,5):
        my_url='https://askubuntu.com/questions/'+str(df.iloc[question,4])
        uClient=uReq(my_url)
        page_html=uClient.read()
        uClient.close()
        page_soup=soup(page_html,"html.parser")
        answer_summary=page_soup.findAll("div",{"class":"post-text"})
        complete_answer=""
        
        complete_answer=complete_answer+" "+str(answer_summary[1]) #consider only the 1st answer since it has max upvotes
        temp=re.sub(r'<a href.*</a>'," ",str(complete_answer))
        temp=re.sub(r'<code>.*</code>'," ",str(temp))
        temp=re.sub(r'<.{1,50}>'," ",str(temp))
        temp=re.sub(r'\. '," ",str(temp))
        temp=temp.replace("(","").replace(")","").replace(",","").replace("/"," ").replace("\\"," ").replace("!"," ").replace(":","")
        temp=' '.join(temp.split())
        
        tempList=[word for word in str(temp).lower().split() if not word in stopwords.words('english')]
       
        tempVar=([i in tagSynonymDict for i in tempList]) #list of boolean values 
        
        for i in range(len(tempVar)):
            if tempVar[i]:
                tempList[i]=tagSynonymDict[tempList[i]]
                #answerTagDict1[df.iloc[question,4]]=Counter(tempList)
                answerTagDict[df.iloc[question,4]]= dict((k,v) for k,v in Counter(tempList).items() if k in set(tagSet))
        


# In[100]:


sum(answerTagDict[162075].values())


# In[52]:


#TESTING PURPOSE
fruits = ['apples', 'bananas', 'pears']
fruit_dict1 = {'apples': 4, 'oranges': 3, 'dragonfruit': 4,'pinnaple':20}
tempVar=([i in fruit_dict1 for i in fruits])
print(x)  
for i in range(len(tempVar)):
    if tempVar[i]:
        fruits[i]=fruit_dict1[fruits[i]]
print(fruits)


# In[49]:


print(fruits)


# In[44]:


len(tagSet)


# In[131]:


df1=pd.DataFrame(columns=['Question Name','No.of Views','No.of answers','No.of Votes',
                          'Question id','Tags By User','Date','Urls','Tags In Question','Tags In Answer','Votes on Answer','Tag Count'])


# In[132]:


len(df1)


# In[155]:


count=0
tempStorage=0

start_time=calendar.timegm(time.gmtime())
answerTagDict={}

for question in range(0,20):
    try:
        my_url='https://askubuntu.com/questions/'+str(df.iloc[question,4])
        uClient=uReq(my_url)
        page_html=uClient.read()
        uClient.close()
        page_soup=soup(page_html,"html.parser")
        answer_summary=page_soup.findAll("div",{"class":"post-text"})
        complete_answer=""
        
        complete_answer=complete_answer+" "+str(answer_summary[1])
        temp=re.sub(r'<a href.*</a>'," ",str(complete_answer))
        temp=re.sub(r'<code>.*</code>'," ",str(temp))
        temp=re.sub(r'<.{1,50}>'," ",str(temp))
        temp=re.sub(r'\. '," ",str(temp))
        temp=temp.replace("(","").replace(")","").replace(",","").replace("/"," ").replace("\\"," ").replace("!"," ").replace(":","")
        temp=' '.join(temp.split())
        temp=temp.lower()
        
        tempSet=set(temp.split())
        #print(tempSet)
        
        #Upvotes
        upvoteValue=page_soup.find_all("div",{"itemprop":"upvoteCount"})

        upvoteValue=str(re.findall(r'>.*</div>',str(upvoteValue[1]))[0]).replace(">","").replace("</div","")
        
        questionKeywords=[word for word in str(df.iloc[question,0]).lower().split() if not word in stopwords.words('english')]
        
        #remove the stop words from the answer
        #tempList=[word for word in str(temp).lower().split() if not word in stopwords.words('english')]
        tempList=[word for word in str(temp).lower().split() if word in list(tagSet)]
        
        if question==8:
            print(tempList)
            print('------------------------------------------------------------------')
       
        tempVar=([i in tagSynonymDict for i in tempList]) #list of boolean values to check if the word in tempList has synonym
        
        for i in range(len(tempVar)):
            if tempVar[i] and (tagSynonymDict[tempList[i]] in tagSet):
                tempList[i]=tagSynonymDict[tempList[i]] #replace synonym words in the list with a same word

                
        answerTagDict[df.iloc[question,4]]= dict((k,v) for k,v in Counter(tempList).items() if k in set(tagSet))
        
        if question==8:
            print(tempList)
            print("answer tags are: "+str(tagSet.intersection(tempSet)))

        
        #question name, no.of views,no.of answers,no. of votes,question id,tags by user,date,urls,tags in question,tags in answer,votes on answer
        df1.loc[question]=str(df.iloc[question,0]),str(df.iloc[question,1]),str(df.iloc[question,2]),str(df.iloc[question,3]),str(df.iloc[question,4]),str(df.iloc[question,5]),str(df.iloc[question,6]),str(my_url),str(tagSet.intersection(set(questionKeywords))), str(tagSet.intersection(tempSet)),str(upvoteValue),str(answerTagDict[df.iloc[question,4]])
        
        print("Question No: "+str(count))
        count=count+1
        tempStorage=tempStorage+1
        if tempStorage==500:
            df1.iloc[(count-500):count].to_csv('TagDataNew'+str(count)+'.csv')  #Create csv file for every 500 questions
            tempStorage = 0
         
    except Exception as e:
        print("Error is "+str(e))
        break
        end_time=calendar.timegm(time.gmtime())
        total_time=(end_time-start_time)/3600
        print("Total time taken before  "+str(count)+" questions in hours: "+str(total_time))
        print(e)
        print("Delay of 180 secs")
        time.sleep(180)

end_time=calendar.timegm(time.gmtime())
total_time=(end_time-start_time)/3600
print("Total time taken after all questions "+str(count)+" questions in hours: "+str(total_time))
df1.to_csv('CompleteTagData.csv')
print("Successfully Saved ALL QUESTIONS"+str(count))


 


# In[156]:


#Create data structure coloums for tf idf values for each tag for answer to each question
len(tagSet)


# In[164]:


set(tagSynonymDict.keys())


# In[179]:


len(tagSet-set(tagSynonymDict.keys()))


# In[173]:


len(set(tagSynonymDict.values()))


# In[176]:


(tagSet | set(tagSynonymDict.values()))-set(tagSet) #critical for assesment


# In[238]:


#THE  CRITICAL LINE 
question='question'
#tagColoumn=list( ( tagSet-set(tagSynonymDict.keys()) ) | ( (tagSet | set(tagSynonymDict.values()))-set(tagSet) ) )
tagColoumn=list(  (tagSet | set(tagSynonymDict.values())))   #critical line containg all tags
tagColoumn.insert(0,question)
tfIdfDataFrame=pd.DataFrame(columns=tagColoumn)
print(tfIdfDataFrame)


# In[239]:


len(tagColoumn)


# In[277]:


totalRows=len(completeTagData)
tagIdf=dict.fromkeys(list( (tagSet | set(tagSynonymDict.values()))) ,0)   #all tags entered to tagIdf dictionary

for i in range (len(completeTagData)):
    for j in (list(   set(ast.literal_eval(completeTagData[i]).keys()).intersection(tagIdf.keys()) ) ):
        tagIdf[j]=tagIdf[j]+1

        
for i in tagIdf.keys():
    if tagIdf[i]!=0:
        tagIdf[i]=math.log10(totalRows/tagIdf[i])


# In[276]:


len(tagTfIdf)


# In[280]:


#calculate idf for each tag
#This is correct
completeTagData=df=pd.read_csv('CompleteTagData.csv',encoding = "ISO-8859-1")
completeTagData=completeTagData.iloc[:,[1,12]]
completeTagData.head()


# In[296]:


tagTfIdf={}
tempData={}
for i in range (len(completeTagData)):
    for key,value in ast.literal_eval(completeTagData.iloc[i,1]).items():
        tempData[key]=(value)*(tagIdf[key])
    tagTfIdf[completeTagData.iloc[i,0]]= tempData
    tempData={}
        


# In[297]:


tagTfIdf


# In[301]:


new_path = 'C:\\Users\\gauth\\AnacondaProjects\\Stack ApiV1\\AllTagExtractionCodeV4\\tfIdf.txt'
tfIdfFile = open(new_path,'w')
tfIdfFile.write(str(tagTfIdf))
tfIdfFile.close()

