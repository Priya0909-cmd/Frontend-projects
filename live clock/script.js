let hourHand = document.querySelector(".hour");
let minHand = document.querySelector(".min");
let secondHand = document.querySelector(".sec");

function setDate(){
    const now = new Date();

    const sec = now.getSeconds();
    const secDeg = ((sec/60)*360) + 90;
    secondHand.style.transform = `rotate(${secDeg}deg)`;

    const min = now.getMinutes();
    const minDeg = ((min/60)*360) + ((sec/60)*6) + 90;
    minHand.style.transform = `rotate(${minDeg}deg)`;

    const hour = now.getHours();
    const HourDeg = ((hour/12)*360) + ((min/60)*30)+ 90 ;
    hourHand.style.transform = `rotate(${HourDeg}deg)`;

    if (sec === 0) {
    secondHand.style.transition = 'none';
  } else {
    secondHand.style.transition = 'transform 0.05s cubic-bezier(0.2, 2, 0.58, 1)';
  }
}
setInterval(setDate, 1000);

setDate();