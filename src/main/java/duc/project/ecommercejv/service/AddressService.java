package duc.project.ecommercejv.service;

import duc.project.ecommercejv.dto.order.AddressRequest;
import duc.project.ecommercejv.dto.order.AddressResponse;
import duc.project.ecommercejv.entity.auth.User;
import duc.project.ecommercejv.entity.order.Address;
import duc.project.ecommercejv.exception.NotFoundException;
import duc.project.ecommercejv.repository.order.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses(User user) {
        return addressRepository.findByUserId(user.getId()).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse create(User user, AddressRequest req) {
        Address address = new Address();
        address.setUser(user);
        applyRequest(address, req);

        if (Boolean.TRUE.equals(req.getIsDefault())) {
            unsetOtherDefaults(user.getId());
        }

        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public void delete(User user, Long id) {
        Address address = getOwnedAddress(user, id);
        addressRepository.delete(address);
    }

    @Transactional
    public AddressResponse update(User user, Long id, AddressRequest req) {
        Address address = getOwnedAddress(user,id);
        applyRequest(address,req);

        if (Boolean.TRUE.equals(req.getIsDefault())) {
            unsetOtherDefaults(user.getId());
        }

        return toResponse(addressRepository.save(address));
    }

//    HELPER
    private Address getOwnedAddress(User user, Long id) {
        Address address = addressRepository.findById(id).orElseThrow(() -> new NotFoundException("Cannot find address"));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new NotFoundException("Cannot find address");
        }
        return address;
    }

    private void unsetOtherDefaults(Long userId) {
        addressRepository.findByUserId(userId).forEach(a -> {
            if (Boolean.TRUE.equals(a.getIsDefault())) {
                a.setIsDefault(false);
                addressRepository.save(a);
            }
        });
    }

    private void applyRequest(Address address, AddressRequest req) {
        address.setRecipientName(req.getRecipientName());
        address.setPhone(req.getPhone());
        address.setProvince(req.getProvince());
        address.setDistrict(req.getDistrict());
        address.setWard(req.getWard());
        address.setDetail(req.getDetail());
        address.setIsDefault(Boolean.TRUE.equals(req.getIsDefault()));
    }

    private AddressResponse toResponse(Address a) {
        return new AddressResponse(a.getId(), a.getRecipientName(), a.getPhone(), a.getProvince(), a.getDistrict(), a.getWard(), a.getDetail(), a.getIsDefault());
    }
}
