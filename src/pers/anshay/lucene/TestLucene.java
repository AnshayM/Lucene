package pers.anshay.lucene;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.Highlighter.Highlight;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexReader.CacheHelper;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.index.DirectoryReader;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * @author Anshay
 * @createDate 2018年9月6日
 */
public class TestLucene {

	public static void main(String[] args) throws Exception {
		// 准备中文分词器
		IKAnalyzer analyzer = new IKAnalyzer();

		// 索引
		List<String> productNames = new ArrayList<>();
		productNames.add("飞利浦led灯泡e27螺口暖白球泡灯家用照明超亮节能灯泡转色温灯泡");
		productNames.add("飞利浦led灯泡e14螺口蜡烛灯泡3W尖泡拉尾节能灯泡暖黄光源Lamp");
		productNames.add("雷士照明 LED灯泡 e27大螺口节能灯3W球泡灯 Lamp led节能灯泡");
		productNames.add("飞利浦 led灯泡 e27螺口家用3w暖白球泡灯节能灯5W灯泡LED单灯7w");
		productNames.add("飞利浦led小球泡e14螺口4.5w透明款led节能灯泡照明光源lamp单灯");
		productNames.add("飞利浦蒲公英护眼台灯工作学习阅读节能灯具30508带光源");
		productNames.add("欧普照明led灯泡蜡烛节能灯泡e14螺口球泡灯超亮照明单灯光源");
		productNames.add("欧普照明led灯泡节能灯泡超亮光源e14e27螺旋螺口小球泡暖黄家用");
		productNames.add("聚欧普照明led灯泡节能灯泡e27螺口球泡家用led照明单灯超亮光源");
		
		
		Directory index = createIndex(analyzer, productNames);

		// 查询器
		String keyword = "护眼带光源";
		Query query = new QueryParser("name", analyzer).parse(keyword);

		// 搜索
		IndexReader reader = DirectoryReader.open(index);
		// 基于render创建搜索器
		IndexSearcher searcher = new IndexSearcher(reader);
		// 指定每页显示数据条数
		int numberPerpager = 1000;
		System.out.println("当前一共有" + productNames.size() + "条数据");
		System.out.println("查询关键字是：" + keyword);
		// 执行搜索
		ScoreDoc[] hits = searcher.search(query, numberPerpager).scoreDocs;
		// 显示查询结果
		showSearchResults(searcher, hits, query, analyzer);
	}

	private static void showSearchResults(IndexSearcher searcher, ScoreDoc[] hits, Query query, IKAnalyzer analyzer)
			throws IOException, InvalidTokenOffsetsException {
		System.out.println("找到 " + hits.length + " 个命中.");
		System.out.println("序号\t匹配度得分\t结果");
		SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));

		for (int i = 0; i < hits.length; ++i) {
			ScoreDoc scoreDoc = hits[i];
			int docID = scoreDoc.doc;
			Document d = searcher.doc(docID);
			List<IndexableField> fields = d.getFields();
			System.out.print(i + 1);
			System.out.print("\t" + scoreDoc.score);
			for (IndexableField f : fields) {
				TokenStream tokenStream = analyzer.tokenStream(f.name(), new StringReader(d.get(f.name())));
                String fieldContent = highlighter.getBestFragment(tokenStream, d.get(f.name()));  
				System.out.print("\t" + d.get(f.name()));
			}
			System.out.println();
		}
	}

	private static Directory createIndex(IKAnalyzer analyzer, List<String> productNames) throws IOException {
		// TODO Auto-generated method stub
		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(index, config);

		for (String name : productNames) {
			addDoc(writer, name);
		}

		writer.close();
		return index;
	}

	private static void addDoc(IndexWriter writer, String name) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("name", name, Field.Store.YES));
		// TODO Auto-generated method stub
		writer.addDocument(doc);

	}
}
