package by.jwd.task6.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import by.jwd.task6.dao.DaoException;
import by.jwd.task6.dao.HotelDao;
import by.jwd.task6.dao.impl.HotelDaoImpl;
import by.jwd.task6.entity.Hotel;
import by.jwd.task6.entity.Room;
import by.jwd.task6.entity.RoomType;
import by.jwd.task6.service.HotelService;
import by.jwd.task6.service.InvalidInputServiceException;
import by.jwd.task6.service.ServiceException;
import by.jwd.task6.util.DataMapKey;

public class HotelServiceImpl implements HotelService {
    
    private HotelDao hotelDao = new HotelDaoImpl();

    @Override       // ИЛИ СОБИРАТЬ Address в команде, чтобы было меньше переменных в параметрах??
    public Optional<Hotel> registerHotel(Map<String, String> data) throws ServiceException, InvalidInputServiceException{
        Optional<Hotel> hotelOptional = Optional.empty();
        // TODO new validation

        try {
            hotelDao.registerHotel(data);
            hotelOptional = findHotelByHotelierId(Integer.valueOf(data.get("userId")));
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
        return hotelOptional;
    }

    @Override
    public Optional<Hotel> findHotelByHotelierId(int hotelierId) throws ServiceException {
        Optional<Hotel> hotelOptional = Optional.empty();
        try {
            hotelOptional = hotelDao.findHotelByHotelierId(hotelierId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
        return hotelOptional;
    }

    @Override
    public void insertHotelPhoto(String path, int hotelId) throws ServiceException {
        try {
            hotelDao.insertHotelPhoto(hotelId, path);
        } catch (DaoException e) {
            throw new ServiceException();
        }
        
    }

    @Override
    public void insertMultipleTypeRooms(int hotelId, List<String> roomTypes, List<String> numberOfRooms,
                                                    List<String> prices) throws ServiceException {
        Map<RoomType, Room> rooms = new HashMap<>();
        Map<RoomType, Integer> roomsNum = new HashMap<>();
        putDataToMaps(rooms, roomsNum, roomTypes, numberOfRooms, prices);
        try {
            hotelDao.insertMultipleTypeRooms(hotelId, rooms, roomsNum);
        } catch (DaoException e) {
            throw new ServiceException();
        }
    }
    
    private void putDataToMaps(Map<RoomType, Room> rooms, Map<RoomType, Integer> roomsNum, List<String> roomTypes, List<String> numberOfRooms,
                                                    List<String> prices) throws ServiceException {
        for (int i = 0; i < roomTypes.size(); i++) {
            RoomType type = resolveRoomType(roomTypes.get(i));
            int roomsNumber = Integer.valueOf(numberOfRooms.get(i));
            BigDecimal price = new BigDecimal(prices.get(i));
            
            Room room = new Room(type, price);
            if (rooms.containsKey(type)) {
                int sum = roomsNum.get(type) + roomsNumber;
                roomsNum.put(type, sum);
            } else {
                rooms.put(type, room);
                roomsNum.put(type, roomsNumber);
            }
        }
    }
    
    private RoomType resolveRoomType(String roomTypeIndex) throws ServiceException {
        int typeIndex = Integer.valueOf(roomTypeIndex);
        Optional<RoomType> typeOptional = RoomType.getRoomTypeByIndex(typeIndex);
        if(typeOptional.isEmpty()) {
            throw new ServiceException("wrong index of room type " + typeIndex);
        }
        RoomType type = typeOptional.get();
        return type;
    }
    
    @Override
    public void insertMeals(int hotelId, Map<String, String> meals) throws ServiceException {
        //TODO validation
        try {
            hotelDao.insertMealTypes(hotelId, meals);
        } catch (DaoException e) {
            throw new ServiceException(e);
            
        }
    }

    @Override
    public Optional<List<Hotel>> findHotelsByUserSearch(Map<String, String> data) throws ServiceException {
        // TODO validation
        try {
            String input = data.get(DataMapKey.INPUT);
            LocalDate localDate = LocalDate.parse(data.get(DataMapKey.FROM));
            long from = localDate.toEpochDay();
            localDate = LocalDate.parse(data.get(DataMapKey.TILL));
            long till = localDate.toEpochDay();

            Optional<List<Hotel>> hotelsOptional = hotelDao.findHotels(input, from, till);
            return hotelsOptional;
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

}
