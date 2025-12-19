# create-structure.ps1
$base = Join-Path "src\main\java" "com\lex\cinema"

$dirs = @(
  $base,
  "$base\config",
  "$base\model",
  "$base\repository",
  "$base\repository\jdbctemplate",
  "$base\repository\jdbcclient",
  "$base\service",
  "$base\service\impl",
  "$base\web",
  "$base\web\dto",
  "$base\web\error"
)

foreach ($d in $dirs) { New-Item -ItemType Directory -Force -Path $d | Out-Null }

$files = @(
  "$base\CinemaApplication.java",
  "$base\config\DbConfig.java",
  "$base\model\MovieSession.java",
  "$base\model\Seat.java",
  "$base\model\Reservation.java",

  "$base\repository\MovieSessionDao.java",
  "$base\repository\SeatDao.java",
  "$base\repository\ReservationDao.java",

  "$base\repository\jdbctemplate\JdbcTemplateMovieSessionDao.java",
  "$base\repository\jdbctemplate\JdbcTemplateSeatDao.java",
  "$base\repository\jdbcclient\JdbcClientReservationDao.java",

  "$base\service\MovieSessionService.java",
  "$base\service\BookingService.java",
  "$base\service\impl\MovieSessionServiceImpl.java",
  "$base\service\impl\BookingServiceImpl.java",

  "$base\web\dto\MovieSessionCreateRequest.java",
  "$base\web\dto\MovieSessionUpdateRequest.java",
  "$base\web\dto\MovieSessionListResponse.java",
  "$base\web\dto\ReservationCreateRequest.java",
  "$base\web\dto\PagedResponse.java",

  "$base\web\SessionsRestController.java",
  "$base\web\ReservationsRestController.java",
  "$base\web\ApiExceptionHandler.java",

  "$base\web\error\NotFoundException.java",
  "$base\web\error\ConflictException.java"
)

foreach ($f in $files) { New-Item -ItemType File -Force -Path $f | Out-Null }

Write-Host "OK: created structure under $base"
