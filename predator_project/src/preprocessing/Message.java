package preprocessing;


public class Message {
	private String line; 
	private String author;
	private String time;
	private String text;
	
	
	public String getLine() {
		return line;
	}
	
	public void setLine(String line) {
		this.line = line;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String unit) {
		this.time = unit;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}


	@Override
	public String toString() {
		return "Item [text=" + text + ", line=" + line
				 + ", author=" + author + ", time=" + time + "]";
	}
}
