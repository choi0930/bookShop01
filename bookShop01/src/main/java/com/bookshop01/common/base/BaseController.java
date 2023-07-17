package com.bookshop01.common.base;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.bookshop01.goods.vo.ImageFileVO;

public abstract class BaseController  {
	private static final String CURR_IMAGE_REPO_PATH = "C:\\shopping\\file_repo";
	
	protected List<ImageFileVO> upload(MultipartHttpServletRequest multipartRequest) throws Exception{
		List<ImageFileVO> fileList= new ArrayList<ImageFileVO>();
		Iterator<String> fileNames = multipartRequest.getFileNames();
		while(fileNames.hasNext()){
			ImageFileVO imageFileVO =new ImageFileVO();
			String fileName = fileNames.next();
			imageFileVO.setFileType(fileName);
			MultipartFile mFile = multipartRequest.getFile(fileName);
			String originalFileName=mFile.getOriginalFilename();
			imageFileVO.setFileName(originalFileName);
			fileList.add(imageFileVO);
			
			File file = new File(CURR_IMAGE_REPO_PATH +"\\"+ fileName);
			if(mFile.getSize()!=0){ //File Null Check
				if(! file.exists()){ //경로상에 파일이 존재하지 않을 경우
					if(file.getParentFile().mkdirs()){ //경로에 해당하는 디렉토리들을 생성
							file.createNewFile(); //이후 파일 생성
					}
				}
				mFile.transferTo(new File(CURR_IMAGE_REPO_PATH +"\\"+"temp"+ "\\"+originalFileName)); //임시로 저장된 multipartFile을 실제 파일로 전송
			}
		}
		return fileList;
	}
	
	private void deleteFile(String fileName) {
		File file =new File(CURR_IMAGE_REPO_PATH+"\\"+fileName);
		try{
			file.delete();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping(value="/*.do" ,method={RequestMethod.POST,RequestMethod.GET})
	protected  ModelAndView viewForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		return mav;
	}
	
	
	protected String calcSearchPeriod(String fixedSearchPeriod){
		String beginDate=null;
		String endDate=null;
		String endYear=null;
		String endMonth=null;
		String endDay=null;
		String beginYear=null;
		String beginMonth=null;
		String beginDay=null;
		//10진수, 값이 없는 자리는 0으로 채움 ex) 7-> 07
		DecimalFormat df = new DecimalFormat("00");
		Calendar cal=Calendar.getInstance();
		//현재 년도 int-> String으로
		endYear   = Integer.toString(cal.get(Calendar.YEAR));
		//Calender.MONTH는 0~11이 반환되므로 현재 월을 알기위해 +1  DecimalFormat으로 빈자리에 0을 채움 7 -> 07 
		endMonth  = df.format(cal.get(Calendar.MONTH) + 1);
		//현재 날짜 구함 DecimalFormat으로 빈자리에 0을 채움 7 -> 07
		endDay   = df.format(cal.get(Calendar.DATE));
		// 예시: 2023-07-17 endDate에는 현재 년월일이 설정됨
		endDate = endYear +"-"+ endMonth +"-"+endDay;
		
		//첫 실행시에는 fixdSearchPeriod가 null이므로 4개월치 만 검색됨
		if(fixedSearchPeriod == null) {
			cal.add(cal.MONTH,-4);
		}else if(fixedSearchPeriod.equals("one_week")) {
			//현재 년도의 날짜 -7일 
			cal.add(Calendar.DAY_OF_YEAR, -7);
		}else if(fixedSearchPeriod.equals("two_week")) {
			//현재 년도의 날짜 -14일
			cal.add(Calendar.DAY_OF_YEAR, -14);
		}else if(fixedSearchPeriod.equals("one_month")) {
			//현재 년도의 월 -1
			cal.add(cal.MONTH,-1);
		}else if(fixedSearchPeriod.equals("two_month")) {
			//현재 년도의 월 -2
			cal.add(cal.MONTH,-2);
		}else if(fixedSearchPeriod.equals("three_month")) {
			//현재 년도의 월 -3
			cal.add(cal.MONTH,-3);
		}else if(fixedSearchPeriod.equals("four_month")) {
			//현재 년도의 월 -4
			cal.add(cal.MONTH,-4);
		}
		//현재 년도
		beginYear   = Integer.toString(cal.get(Calendar.YEAR));
		//위에서 설정한 월 or 날짜
		beginMonth  = df.format(cal.get(Calendar.MONTH) + 1);
		beginDay   = df.format(cal.get(Calendar.DATE));
		//예시 2023-06-17 
		beginDate = beginYear +"-"+ beginMonth +"-"+beginDay;
		//,을 구분자로 검색일 시작 날짜와 끝날짜를 반환
		return beginDate+","+endDate;
	}
	
}
