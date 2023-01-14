package com.ciosmak.bankapp.service;

import com.ciosmak.bankapp.entity.Address;
import com.ciosmak.bankapp.entity.User;
import com.ciosmak.bankapp.exception.FatalError;
import com.ciosmak.bankapp.repository.UserRepository;
import com.ciosmak.bankapp.user.id.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Scanner;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
/**
 * <h2>Actions on the user's address</h2>
 */
public class AddressService extends AbstractService
{
    /**
     * Change existing address
     * @param userId user id from database (singleton)
     */
    public void changeAddress(UserId userId)
    {
        User user = getUserById(userId, userRepository);

        Address address = createAddress("\n---ZMIANA ADRESU---", false);
        user.getAddresses().get(0).setStreet(address.getStreet());
        user.getAddresses().get(0).setHouseNumber(address.getHouseNumber());
        user.getAddresses().get(0).setApartmentNumber(address.getApartmentNumber());
        user.getAddresses().get(0).setPostCode(address.getPostCode());
        user.getAddresses().get(0).setTown(address.getTown());
        user.getAddresses().get(0).setCountry(address.getCountry());
        System.out.println("ADRES ZOSTAŁ ZMIENIONY POMYŚLNIE");
    }

    /**
     * Create or change if exists the mailing address
     * @param userId user id from database (singleton)
     */
    public void changeMailingAddress(UserId userId)
    {
        User user = getUserById(userId, userRepository);

        if (user.getAddresses().size() < 2)
        {
            char addMailingAddress;
            while (true)
            {
                System.out.println("Adres korespodencyjny jest taki sam jak adres główny.");
                System.out.println("Chcesz ustawić inny adres do korespondencji (T/N): ");
                addMailingAddress = scanner.nextLine().charAt(0);
                if (trueOrFalseAnswerIsCorrect(addMailingAddress))
                {
                    if (Character.toUpperCase(addMailingAddress) == 'T')
                    {
                        Address address = createAddress("\n---DODAWANIE ADRESU KORESPONDENCYJNEGO---", true);
                        user.getAddresses().add(address);
                    }
                    else
                    {
                        System.out.println("Żadne zmiany nie zostały wprowadzone");
                    }
                    return;
                }
                else
                {
                    System.err.println("Nie ma takiej opcji.\nNależy wprowadzić znak 'T' lub znak 'N'.\nSpróbuj ponownie.");
                    System.err.flush();
                }
            }
        }
        Address address = createAddress("---ZMIANA ADRESU KORESPONDENCYJNEGO---", true);
        user.getAddresses().get(1).setStreet(address.getStreet());
        user.getAddresses().get(1).setHouseNumber(address.getHouseNumber());
        user.getAddresses().get(1).setApartmentNumber(address.getApartmentNumber());
        user.getAddresses().get(1).setPostCode(address.getPostCode());
        user.getAddresses().get(1).setTown(address.getTown());
        user.getAddresses().get(1).setCountry(address.getCountry());
        System.out.println("ADRES KORESPONDENCYJNY ZOSTAŁ ZMIENIONY POMYŚLNIE");
    }

    /**
     * Create a new address
     * @param label header displayed at the beginning
     * @param isMailing information if this address isMailing
     * @return created address
     */
    Address createAddress(String label, boolean isMailing)
    {
        Address address = new Address();
        System.out.println(label);

        String street;
        System.out.print("Podaj ulicę: ");
        street = scanner.nextLine();
        if(checkIfVarcharLengthIsNotCorrect(street))
        {
            FatalError.exit();
        }
        street = capitalizeFirstLetterOfEveryWord(street);
        address.setStreet(street);

        String houseNumber;
        System.out.print("Podaj numer domu: ");
        houseNumber = scanner.nextLine();
        if(checkIfVarcharLengthIsNotCorrect(houseNumber))
        {
            FatalError.exit();
        }
        houseNumber = convertAllLettersToUppercase(houseNumber);
        address.setHouseNumber(houseNumber);

        String apartmentNumber;
        System.out.print("Podaj numer mieszkania: ");
        apartmentNumber = scanner.nextLine();
        if(checkIfVarcharLengthIsNotCorrect(apartmentNumber))
        {
            FatalError.exit();
        }
        apartmentNumber = convertAllLettersToUppercase(apartmentNumber);
        address.setApartmentNumber(apartmentNumber);

        String postCode;
        System.out.print("Podaj kod pocztowy: ");
        while (true)
        {
            postCode = scanner.nextLine();
            postCode = preparePostCode(postCode);
            if (postCodeIsCorrect(postCode))
            {
                break;
            }
            else
            {
                System.err.println("Podany kod pocztowy jest błędny.\nKod pocztowy powinien się składać tylko z 5 cyfr.");
                System.err.flush();
                System.out.print("Ponownie podaj kod pocztowy: ");
            }
        }
        address.setPostCode(postCode);

        String town;
        System.out.print("Podaj miejscowość: ");
        town = scanner.nextLine();
        if(checkIfVarcharLengthIsNotCorrect(town))
        {
            FatalError.exit();
        }
        town = capitalizeFirstLetterOfEveryWord(town);
        address.setTown(town);

        String country;
        System.out.print("Podaj kraj: ");
        country = scanner.nextLine();
        if(checkIfVarcharLengthIsNotCorrect(country))
        {
            FatalError.exit();
        }
        country = capitalizeFirstLetterOfEveryWord(country);
        address.setCountry(country);

        address.setMailing(isMailing);
        return address;
    }

    /**
     * Create a mailing address (using createAddress method)
     * @param addresses User address list
     * @return User address list with new mailing address
     */
    ArrayList<Address> createMailingAddress(ArrayList<Address> addresses)
    {
        char mailingAddressIsDifferent;
        while (true)
        {
            try
            {
                System.out.print("Chcesz podać inny adres do kontaktu (T/N): ");
                mailingAddressIsDifferent = scanner.nextLine().charAt(0);
                if (trueOrFalseAnswerIsCorrect(mailingAddressIsDifferent))
                {
                    if (Character.toUpperCase(mailingAddressIsDifferent) == 'T')
                    {
                        addresses.add(createAddress("---WPROWADŹ ADRES KORESPONDENCYJNY---", true));
                    }
                    break;
                }
                else
                {
                    System.err.println("Nie ma takiej opcji.\nNależy wprowadzić znak 'T' lub znak 'N'.\nSpróbuj ponownie.");
                    System.err.flush();
                }
            }
            catch (Exception e)
            {
                scanner = new Scanner(System.in);
                System.err.println("Nie ma takiej opcji.\nNależy wprowadzić znak 'T' lub znak 'N'.\nSpróbuj ponownie.");
                System.err.flush();
            }
        }
        return addresses;
    }

    /**
     * Prepare post code format
     * @param postCode user-entered post code
     * @return formatted post code
     */
    private String preparePostCode(String postCode)
    {
        if (postCode.charAt(2) == '-')
        {
            return postCode;
        }
        return postCode.substring(0, 2) + "-" + postCode.substring(2);
    }

    /**
     * Check if post code is correct
     * @param postCode formatted post code
     * @return true if post code is correct and false if post code isn't correct
     */
    private boolean postCodeIsCorrect(String postCode)
    {
        if (postCode.length() == 6)
        {
            for (int i = 0; i < postCode.length(); ++i)
            {
                if (i == 2)
                {
                    continue;
                }
                if (!Character.isDigit(postCode.charAt(i)))
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private final UserRepository userRepository;
}
