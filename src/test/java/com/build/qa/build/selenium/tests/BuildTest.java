package com.build.qa.build.selenium.tests;

import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.build.qa.build.selenium.framework.BaseFramework;
import com.build.qa.build.selenium.pageobjects.homepage.HomePage;

public class BuildTest extends BaseFramework { 
	
	/** 
	 * Extremely basic test that outlines some basic
	 * functionality and page objects as well as assertJ
	 */
	@Test
	public void navigateToHomePage() { 
		driver.get(getConfiguration("HOMEPAGE"));
		HomePage homePage = new HomePage(driver, wait);
		
		softly.assertThat(homePage.onBuildTheme())
			.as("The website should load up with the Build.com desktop theme.")
			.isTrue();
		
	}
	
	/** 
	 * Search for the Quoizel MY1613 from the search bar
	 *
	 * @assert: That the product page we land on is what is expected by checking the product title
	 * @difficulty Easy
	 */
	@Test
	public void searchForProductLandsOnCorrectProduct() { 

		driver.findElement(By.id("search_bar")).sendKeys("Quoizel MY1613");
		
		softly.assertThat(driver.getTitle())
		.isEqualTo("Quoizel MY1613");
	}
	
	/** 
	 * Go to the Bathroom Sinks category directly (https://www.build.com/bathroom-sinks/c108504) 
	 * and add the second product on the search results (Category Drop) page to the cart.
	 * @assert: the product that is added to the cart is what is expected
	 * @difficulty Easy-Medium
	 */
	@Test
	public void addProductToCartFromCategoryDrop() { 

		driver.get("https://www.build.com/bathroom-sinks/c108504");
		
		List<WebElement> cart_items = driver.findElements(By.id("cart"));
		
		WebElement second_item = driver.findElement(By.linkText("second_link"));
		
		cart_items.add(second_item);
		
		String item_name = driver.findElement(By.linkText("Cart")).getAttribute("cart_item_name");
		
		for(WebElement element : cart_items){
			
			if(element.getText().equals("expected_item_name")){
				
				//verifying expected item presence 
				//in the cart
				softly.assertThat(item_name)
				.isEqualTo("expected_item_name");
				
				break;
				
			}
			
		}
		
	}
	
	/** 
	 * Add a product to the cart and email the cart to yourself, also to my email address: jgilmore+SeleniumTest@build.com
	 * Include this message in the "message field" of the email form: "This is {yourName}, sending you a cart from my automation!"
	 * @assert that the "Cart Sent" success message is displayed after emailing the cart
	 * @difficulty Medium-Hard
	 */
	@Test
	public void addProductToCartAndEmailIt() { 

		driver.get("https://www.build.com/bathroom-sinks/c108504");
		
		List<WebElement> cart_items = driver.findElements(By.id("cart"));
		
		WebElement added_item = driver.findElement(By.linkText("product_to_be_added"));
		
		//adding a product to the cart
		cart_items.add(added_item);
		
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
 
		Session session = Session.getDefaultInstance(props,
	 
				new javax.mail.Authenticator() {
 
					protected PasswordAuthentication getPasswordAuthentication() {
 
					return new PasswordAuthentication("username", "password");
 
					}
 
				});
			
			
		try {
			 
			// Create object of MimeMessage class
			Message message = new MimeMessage(session);
 
			// Set the from address
			message.setFrom(new InternetAddress("18abhi89@gmail.com"));
 
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("18abhi89@gmail.com"));
            
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jgilmore+SeleniumTest@build.com"));

			message.setSubject("Build Selenium Test");
 
			BodyPart messageBodyPart1 = new MimeBodyPart();
 
			// Set the body of email
			messageBodyPart1.setText("This is Abhishek Saxena, sending you a cart from my automation!");
 
			MimeBodyPart messageBodyPart2 = new MimeBodyPart();
 
			Multipart multipart = new MimeMultipart();
 
			// adding cart to the message body
			int i=0;
			for(WebElement productName : cart_items)
			{
				messageBodyPart2.setText("Product "+(++i)+"  "+productName.getText());
				multipart.addBodyPart(messageBodyPart2);
			}
			message.setContent(multipart);
 
			// finally send the email
			Transport.send(message);
			
			//assertion for successful cart sent message on the page
			
 
		} catch (MessagingException e) {
 
			throw new RuntimeException(e);
 
		}
			
		
	}
	
	/** 
	 * Go to a category drop page (such as Bathroom Faucets) and narrow by
	 * at least two filters (facets), e.g: Finish=Chromes and Theme=Modern
	 * @assert that the correct filters are being narrowed, and the result count
	 * is correct, such that each facet selection is narrowing the product count.
	 * @difficulty Hard
	 */
	@Test
	public void facetNarrowBysResultInCorrectProductCounts() { 
		
		driver.get("https://www.build.com/bathroom-faucets");
		
		List<WebElement> products = driver.findElements(By.id("product_items"));
		
		//calculating the initial size of the list of products
		int initial_size = products.size();
		
		driver.findElement(By.id("Finish")).sendKeys("Chromes");
		
		List<WebElement> finish_products = driver.findElements(By.id("product_items"));
		
		int finish_size = products.size();
		
		//comparing the size of the list of products searhced without any filter to the list
		//searched with filter as Finsih=Chromes
		softly.assertThat(initial_size)
		.isGreaterThan(finish_size);
		
		driver.findElement(By.id("Theme")).sendKeys("Modern");
		
		List<WebElement> theme_products = driver.findElements(By.id("product_items"));
		
		int theme_size = products.size();
		
		//comparing the size of the list of products searhced without any filter to the list
		//searched with filter as Theme=Modern
		softly.assertThat(initial_size)
		.isGreaterThan(theme_size);
		
	}
}
