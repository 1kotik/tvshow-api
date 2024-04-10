package cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.javaprojects.tvshowapi.cache.EntityCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class EntityCacheTest {
    @Mock
    private Map<Long, String> cache;

    @InjectMocks
    private EntityCache<Long, String> entityCache;

    @Test
    void getTest() {
        when(cache.get(1L)).thenReturn("Alex");
        String result = entityCache.get(1L);

        assertEquals("Alex",result);
    }
    
}
