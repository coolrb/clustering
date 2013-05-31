package main;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainCraw {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Create the news Dir First
		File dir = new File("news");
		if(!dir.exists())
			dir.mkdir();
		
		craw("http://www.guardian.co.uk/world/usa", "news/usa_news.txt");
		craw("http://www.guardian.co.uk/world/africa/roundup", "news/africa_news.txt");
		craw("http://www.guardian.co.uk/world/europe/roundup","news/europe_news.txt");
		craw("http://www.guardian.co.uk/world/middleeast/roundup","news/middleeast_news.txt");
		craw("http://www.guardian.co.uk/world","news/world_news.txt");
		craw("http://www.guardian.co.uk/world/americas/roundup","news/americas_news.txt");
		craw("http://www.guardian.co.uk/world/asiapacific/roundup","news/asiapacific_news.txt");
		craw("http://www.guardian.co.uk/", "news/index_news.txt");
		//Aggregate all the news
		aggregate();
	}
	
	public static void aggregate(){
		StringBuffer sb = new StringBuffer();
		File dir = new File("news");
		if(dir.isDirectory()){
			File[] files = dir.listFiles();
			for (File file : files) {
				addFile(file, sb);
				file.delete();
			}
			dir.delete();
		}else{
			System.err.println("Dir news doesn't exist!");
			return;
		}
		sb.deleteCharAt(sb.length()-1);
		
		String html = sb.toString();
		String items[] = html.split("\n");
		System.out.println(items.length);
		
		HashSet<String> set = new HashSet<String>();		
		StringBuffer fileBuffer = new StringBuffer();
		int count = 0;
		for (String item : items) {
			String []strs = item.split("\t");
			if(!set.contains(strs[0])){
				set.add(strs[0]);
				fileBuffer.append(item);
				fileBuffer.append("\n");
				count++;
			}
		}
		System.out.println(count);
		FileHelper.write("all_news.txt", fileBuffer.toString());
		
		//Delete All The Temp Files
		
	}
	
	public static void addFile(File file , StringBuffer sb){
		System.out.println("Add File : " + file.getName());
		sb.append(FileHelper.read(file.getAbsolutePath()));
		sb.append("\n");
	}

	public static void craw(final String url , final String fileName){
		String html = HttpHelper.http_get(url);
		ArrayList<NewsItem> arr = parseIndex(html);
		StringBuffer fileBuffer = new StringBuffer();
		for (NewsItem item : arr) {
			fileBuffer.append(item.toEncryptString());
			fileBuffer.append("\n");
		}
		fileBuffer.deleteCharAt(fileBuffer.length()-1);
		FileHelper.write(fileName, fileBuffer.toString());
		System.out.println("File " + fileName +" has been written!");
	}
	
	public static ArrayList<NewsItem> parseIndex(String html){
		Document index = Jsoup.parse(html);
		Elements link_text_eles = index.getElementsByClass("link-text");
		final ArrayList<NewsItem> arr = new ArrayList<NewsItem>();
		final HashSet<String> set = new HashSet<String>();
		final ArrayList<Thread> threads_list = new ArrayList<Thread>();
		final int max_http_cons = 5;
		
		for (Element element : link_text_eles) {
			final String href = element.attr("href");
			if(href.contains("2013")){
				Thread th = new Thread(new Runnable() {
					@Override
					public void run() {
						NewsItem item = parseNews(href);
						synchronized (MainCraw.class) {
							if(item != null && !set.contains(href)){
								arr.add(item);
								set.add(href);
							}			
						}
					}
				});
				while(threads_list.size() >= max_http_cons){
					try {
						Thread.sleep(1000);
						clearThreadList(threads_list);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				th.start();
				threads_list.add(th);
			}
		}
		
		try {
			while(!threads_list.isEmpty()){
				Thread.sleep(1000);
				clearThreadList(threads_list);
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return arr;
	}
	
	public static NewsItem parseNews(String newsUrl){
		System.out.println("Parse : " + newsUrl);
		
		String html = null;
		NewsItem item = null;
		try {
			html = HttpHelper.http_get(newsUrl);
			if(html != null){
				Document doc = Jsoup.parse(html);
				Elements head_eles =  doc.getElementsByAttributeValueContaining("itemprop", "name headline");
				String news_name = head_eles.first().html();
				
				// id: article-body-blocks
				Element article_body_blocks = doc.getElementById("article-body-blocks");
				Elements p_eles =  article_body_blocks.getAllElements();
				StringBuffer article = new StringBuffer();
				for (Element ele : p_eles) {
					if(ele.nodeName().equals("p")){
						article.append(ele.html());
					}
				}
				
				StringBuffer sb = new StringBuffer();
				//clear the html tags
				Pattern a_pattern = Pattern.compile("<[a-zA-z]*\\s*[^>]*\\s*>([^<]*)</[a-zA-z]*>");
				//clear the unicode punctuation
				Matcher mat =  a_pattern.matcher(article.toString());
				while(mat.find()){
					mat.appendReplacement(sb, mat.group(1));
				}	
				mat.appendTail(sb);
				//clear the escape character
				String finalStr = sb.toString().replaceAll("&[a-zA-z]*;", "").replaceAll("[\\pP]", "");
				
				item = new NewsItem(newsUrl, news_name, finalStr);
				//item = new NewsItem(newsUrl, news_name, article.toString());
				System.out.println("Parse Over:" + newsUrl);	
			}else{
				System.err.println("Can't parse " + newsUrl );	
			}
		} catch (Exception e) {
			System.err.println("Can't parse " + newsUrl );
		}
				
		return item;
	}
	
	public static void clearThreadList(final ArrayList<Thread> threads_list){
		ArrayList<Thread> tmp_list = new ArrayList<Thread>();
		for (Thread thread : threads_list) {
			if(!thread.isAlive()){
				tmp_list.add(thread);
			}
		}
		threads_list.removeAll(tmp_list);

	}
}
