package cn.keking;

import cn.keking.service.cache.CacheService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FilePreviewApplicationTests {

	@Qualifier("cacheServiceRocksDBImpl")
	@Autowired
	private CacheService cacheService;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testCache(){

		Map<String,String> result = cacheService.getPDFCache();
		System.out.println(result);

		cacheService.cleanCache();
	}

}
