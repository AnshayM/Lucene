# Lucene

![image](https://i.loli.net/2018/09/06/5b90f344610ec.png)

分词器概念：
        分词器指的是搜索引擎如何使用关键字进行匹配，如关键字：护眼带光源。 如果使用like,那么%护眼带光源%，匹配出来的结果就是要么全匹配，要不都不匹配。
        而使用分词器，就会把这个关键字分为 护眼，带，光源 3个关键字，这样就可以找到不同相关程度的结果了。

IKAnalyzer6.5.0.jar
    IKAnalyzer 这个分词器很久都没有维护了，也不支持Lucene7。 IKAnalyzer6.5.0.jar 这个是修改之后的的，可以支持Lucene7的jar。
