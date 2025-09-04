package patterns.command;

import model.Booking;
import model.BookingStatus;
import java.util.function.Consumer;

public class BookingCommand implements Command {
    private final Booking booking;
    private final Consumer<Booking> executeAction;
    private BookingStatus previousStatus;

    public BookingCommand(Booking booking, Consumer<Booking> executeAction) {
        this.booking = booking;
        this.executeAction = executeAction;
    }

    @Override
    public void execute() {
        previousStatus = booking.getBookingStatus();
        executeAction.accept(booking);
    }

    @Override
    public void undo() {
        if (previousStatus != null) {
            booking.setBookingStatus(previousStatus);
        }
    }

    @Override
    public String getDescription() {
        return "Booking command for Room " + booking.getRoomNumber() + 
               " by User " + booking.getUserId();
    }
}