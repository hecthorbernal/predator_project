package xmlImport;


public class ConversationMessage {
	private String line; 
	private String author;
	private String time;
	private String text;
	private int normalized_time;
	/**
	 * @return the normalized_time
	 */
	public int getNormalized_time() {
		return normalized_time;
	}
	/**
	 * @param normalized_time the normalized_time to set
	 */
	public void setNormalized_time(int normalized_time) {
		this.normalized_time = normalized_time;
	}
	private int number_of_lines;
	
	
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
	/**
	 * @return the number_of_lines
	 */
	public int getNumber_of_lines() {
		return number_of_lines;
	}
	/**
	 * @param number_of_lines the number_of_lines to set
	 */
	public void setNumber_of_lines(int number_of_lines) {
		this.number_of_lines = number_of_lines;
	}
	@Override
	public String toString() {
		return "Item [text=" + text + ", line=" + line
				 + ", author=" + author + ", time=" + time + "]";
	}
}
