//package net.engineeringdigest.journalApp.service;
//
//import net.engineeringdigest.journalApp.repository.UserRepository;
//import org.junit.Assert;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.ArrayList;
//
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//public class UserDetailsServiceImplTest {
//
//    @InjectMocks
//    private UserDetailsServiceImpl userDetailsService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @BeforeEach
//    void setUp(){
//        MockitoAnnotations.initMocks(this);
//    }
//
////    @Test
////    void loadUserByUsernameTest() {
////        when(userRepository.findByUserName(ArgumentMatchers.anyString())).thenReturn(User.builder().username("Saurabh")).password(
////                "gyjhabdhhjd").roles(new ArrayList<>()).build();
////        UserDetails user = userDetailsService.loadUserByUsername("ram");
////        Assert.assertNotNull(user);
////    }
//}
