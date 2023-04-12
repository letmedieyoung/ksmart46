package ksmart.mybatis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import ksmart.mybatis.dto.Goods;
import ksmart.mybatis.dto.Member;
import ksmart.mybatis.mapper.GoodsMapper;
import ksmart.mybatis.mapper.MemberMapper;
import ksmart.mybatis.service.GoodsService;
import lombok.extern.slf4j.Slf4j;

//컨트롤러 영역 : 클라이언트의 요청을 받아 해당 요청을 수행하는 데 필요한 로직 호출, 결과 포함 응답을 해주는 디스패처 역할을 한다.
@Controller
@RequestMapping("/goods") //요청에 대한 주소 지정. 클라이언트에서 호출한 주소와 수행할 메소드 연결.
@Slf4j
public class GoodsController {

	private final GoodsService goodsService;
	private final MemberMapper memberMapper;
	private final GoodsMapper goodsMapper;
	
	public GoodsController(GoodsService goodsService, MemberMapper memberMapper, GoodsMapper goodsMapper) {
		this.goodsService = goodsService;
		this.goodsMapper = goodsMapper;
		this.memberMapper = memberMapper;
	}
	
	@PostMapping("/removeGoods")
	public String removeGoods(@RequestParam(name="goodsCode") String goodsCode
							 ,@RequestParam(name="memberId") String memberId
							 ,@RequestParam(name="memberPw") String memberPw
							 ,HttpSession session //웹에 접속하면 코드를 작성하지 않아도 존재한다.
							 ,RedirectAttributes reAttr) { //해당 매개변수를 선언하면 객체가 생성된다. 1회성으로 데이터 전달.
		String memberLevel = (String) session.getAttribute("SLEVEL"); //세션이 유지되는 동안 세션에 저장되어 있는 SLEVEL값을 String 타입으로 memberLevel에 담는다.
		boolean isDelete = true; //boolean타입 변수 isDelete를 true로 선언
		String msg = ""; //String타입 변수 msg에 공백 대입.
		//로그인 아이디의 권한이 판매자인지 확인하고, 특정 상품의 판매자아이디와 로그인 아이디가 동일한지 확인한다.
		if(memberLevel != null && "2".equals(memberLevel)) { //memberLevel이 null값이 아니면서 2와 같다면
			isDelete = goodsMapper.isSellerByGoodsCode(memberId, goodsCode); //isSellerByGoodsCode메서드 반환 값을 isDelete에 대입
		}
		
		Member member = memberMapper.getMemberInfoById(memberId); //getMemberInfoById메서드 반환 값을 Member객체 타입의 member에 담는다.
		if(member != null) { //member가 null값이 아닐 때
			String checkPw = member.getMemberPw(); //getMemberPw(): [회원비밀번호]를 String타입 변수 checkPw에 담는다.
			if(!checkPw.equals(memberPw)) isDelete = false; //checkPw가 memberPw와 같지 않다면, isDelete = false
		}
		if(isDelete) { //isDelete값이 true일 때,
			goodsService.removeGoods(goodsCode); //removeGoods()호출
			msg = "상품코드: "+ goodsCode + " 가 삭제되었습니다.";
		}else { //isDelete값이 false일 때,
			msg = "상품코드: "+ goodsCode + " 가 삭제할 수 없습니다.";			
		}
		reAttr.addAttribute("msg", msg); //redirect객체 전달. 문자열을 넣어 전달하면 '/goods/goodsList?msg='와 같이 URI에 노출된다.
		
		return "redirect:/goods/goodsList"; //요청 수행 완료 시 redirect하여 '/goods/goodsList' 경로로 주소 이동.
	}
	
	@GetMapping("/removeGoods") //removeGoods의 요청을 수행, 응답한다.
	public String removeGoods(Model model //String 타입 메소드 removeGoods 실행. 인터페이스 Model의 model을 매개변수로 한다.
							 ,@RequestParam(name="goodsCode") String goodsCode) { //@RequestParam: 파라미터 이름으로 바인딩. 파라미터 이름: goodsCode
		model.addAttribute("title", "상품삭제"); //model.addAttribute(속성, 값) - 뷰에 데이터 전달.
		model.addAttribute("goodsCode", goodsCode);
		return "goods/removeGoods";
	}
	
	@PostMapping("/modifyGoods")
	public String modifyGoods(Goods goods) {
		goodsService.modifyGoods(goods);
		return "redirect:/goods/goodsList";
	}
	
	@GetMapping("/modifyGoods")
	public String modifyGoods(Model model
							 ,@RequestParam(name="goodsCode") String goodsCode) {
		
		Goods goodsInfo = goodsService.getGoodsInfoByCode(goodsCode);
		
		model.addAttribute("title", "상품수정");
		model.addAttribute("goodsInfo", goodsInfo);
		
		return "goods/modifyGoods";
	}
	
	@PostMapping("/addGoods") //post방식으로 매핑된 메서드 실행
	public String addGoods(Goods goods) { //리턴 데이터타입이 String인 addGoods메소드 실행, 데이터타입 Goods인 goods를 매개변수로 한다.
		goodsService.addGoods(goods); //goodsService에 있는 addGoods메소드에 매개변수 goods를 담아 호출.
		return "redirect:/goods/goodsList"; //redirect하여 주소를 재요청하여 이동한다. /goods/goodsList
	}
	
	@PostMapping("/sellersInfo")
	@ResponseBody
	public List<Member> sellersInfo(){
		String searchKey = "m.m_level";
		String searchValue = "2";
		List<Member> memberList = memberMapper.getMemberList(searchKey, searchValue);
		memberList.forEach(seller -> seller.setMemberPw(""));
		return memberList;
	}
	
	@GetMapping("/addGoods") //클라이언트의 요청에 맞는 주소를 찾아 요청을 수행
	public String addGoods(Model model) { //리턴 데이터타입이 String인 addGoods메소드 실행, 데이터타입 Model인 model을 매개변수로 한다. 
		
		model.addAttribute("title", "상품등록"); //addAttribute 메소드 호출. model객체에 key와 value를 담는다.
		
		return "goods/addGoods"; //goods/addGoods 주소를 반환한다.
	}
		
	@GetMapping("/sellerList")
	public String getGoodsListBySeller( Model model
									   ,@RequestParam(name="checkSearch", required = false) String[] checkArr
									   ,@RequestParam(name="searchValue", required = false) String searchValue) {
		
		List<Member> goodsListBySeller = goodsService.getGoodsListBySeller(checkArr, searchValue);
		model.addAttribute("title", "판매자별상품조회");
		model.addAttribute("goodsListBySeller", goodsListBySeller);
		
		return "goods/sellerList";
	}
	
	@GetMapping("/goodsList")
	public String getGoodsList(Model model
			   				  ,HttpSession session
			   				  ,@RequestParam(name="msg", required = false) String msg) { //required=false로 지정하여 String타입 msg 값만을 받을 수 있다.
		String memberLevel = (String) session.getAttribute("SLEVEL");
		Map<String, Object> paramMap = null; //Map에 배열을 담는다. key:String, value로 최상위 클래스 Object 지정
		if(memberLevel != null && "2".equals(memberLevel)) {
			String sellerId = (String)session.getAttribute("SID");
			
			paramMap = new HashMap<String, Object>();
			paramMap.put("searchKey", "g_seller_id");
			paramMap.put("searchValue", sellerId);
		}
		
		List<Goods> goodsList = goodsService.getGoodsList(paramMap);
		
		model.addAttribute("title", "상품조회");
		model.addAttribute("goodsList", goodsList);
		if(msg != null) model.addAttribute("msg", msg);
		
		return "goods/goodsList";
	}
}
