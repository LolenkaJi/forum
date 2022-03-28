package telran.java41.accounting.service;

import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.java41.accounting.dao.UserAccountRepository;
import telran.java41.accounting.dto.RolesResponseDto;
import telran.java41.accounting.dto.UserAccountResponseDto;
import telran.java41.accounting.dto.UserRegisterDto;
import telran.java41.accounting.dto.UserUpdateDto;
import telran.java41.accounting.dto.exceptions.UserExistsException;
import telran.java41.accounting.dto.exceptions.UserNoTFoundException;
import telran.java41.accounting.model.UserAccount;

@Service
public class UserAccountServiceImpl implements UserAccountService {
	UserAccountRepository repository;
	ModelMapper modelMapper;
	
	@Autowired
	public UserAccountServiceImpl(UserAccountRepository repository, ModelMapper modelMapper) {
		this.repository = repository;
		this.modelMapper = modelMapper;
	}

	@Override
	public UserAccountResponseDto addUser(UserRegisterDto userRegisterDto) {
		if(repository.existsById(userRegisterDto.getLogin())) {
			throw new UserExistsException(userRegisterDto.getLogin());
		}
		
		UserAccount user = modelMapper.map(userRegisterDto, UserAccount.class);
		String password = BCrypt.hashpw(userRegisterDto.getPassword(), BCrypt.gensalt());
		user.setPassword(password);
		repository.save(user);		
		return modelMapper.map(user, UserAccountResponseDto.class);
	}

	@Override
	public UserAccountResponseDto getUser(String login) {
		UserAccount user = repository.findById(login).orElseThrow(()->new UserNoTFoundException());
		return modelMapper.map(user, UserAccountResponseDto.class);
	}

	@Override
	public UserAccountResponseDto removeUser(String login) {
		UserAccount user = repository.findById(login).orElseThrow(()->new UserNoTFoundException());
		repository.delete(user);
		return modelMapper.map(user, UserAccountResponseDto.class);
	}

	@Override
	public UserRegisterDto editUser(String login, UserUpdateDto updateDto) {
		UserAccount user = repository.findById(login).orElseThrow(() -> new UserNoTFoundException());
		if (updateDto.getFirstName() != null) {
			user.setFirstName(updateDto.getFirstName());
		}
		if (updateDto.getLastName() != null) {
			user.setLastName(updateDto.getLastName());
		}
		repository.save(user);
		return modelMapper.map(user, UserRegisterDto.class);
	}

	@Override
	public RolesResponseDto changeRolesList(String login, String role, boolean isAddRole) {
		UserAccount user = repository.findById(login).orElseThrow(() -> new UserNoTFoundException());
		if(isAddRole) {
		user.addRole(role.toUpperCase());
		}
		if(!isAddRole) {
		user.removeRole(role);
		}	
		repository.save(user);
		return modelMapper.map(user, RolesResponseDto.class);
	}

	@Override
	public void changePassword(String login, String newPassword) {
		UserAccount user = repository.findById(login).orElseThrow(() -> new UserNoTFoundException());
		String password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
		user.setPassword(password);
		repository.save(user);

	}

}
