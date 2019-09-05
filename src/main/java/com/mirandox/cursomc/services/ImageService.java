package com.mirandox.cursomc.services;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mirandox.cursomc.services.exceptions.FileException;

@Service
public class ImageService {

	public BufferedImage getJpgImageFromFile(MultipartFile uploadedFile) {
		String extensao = FilenameUtils.getExtension(uploadedFile.getOriginalFilename());
		
		if(!"png".equals(extensao) && !"jpg".equals(extensao))
			throw new FileException("Somente imagens PNG e JPG são permitidas!");
		
		try {
			BufferedImage image = ImageIO.read(uploadedFile.getInputStream());
			
			if("png".equals(extensao)) 
				image = pngToJpg(image);
			
			return image;
		} catch (IOException e) {
			throw new FileException("Erro ao ler arquivo!");
		}
	}

	public BufferedImage pngToJpg(BufferedImage image) {
		BufferedImage jpgImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		jpgImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
		return jpgImage;
	}
	
	public InputStream getInputStream(BufferedImage image, String extensao) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, extensao, os);
			return new ByteArrayInputStream(os.toByteArray());
		} catch(IOException e) {
			throw new FileException("Erro ao ler arquivo!");
		}
	}
	
	public BufferedImage cropSquare(BufferedImage sourceImg) {
		int min = (sourceImg.getHeight() <= sourceImg.getWidth()) ? sourceImg.getHeight() : sourceImg.getWidth();
		return Scalr.crop(sourceImg, 
				(sourceImg.getWidth() / 2) - (min / 2), 
				(sourceImg.getHeight() / 2) - (min / 2), 
				min, min);
	}
	
	public BufferedImage resize(BufferedImage sourceImg, int size) {
		return Scalr.resize(sourceImg, Scalr.Method.ULTRA_QUALITY, size);
	}
}
