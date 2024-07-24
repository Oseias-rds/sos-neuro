import com.example.sosneuromobile.model.AuthResponse
import com.example.sosneuromobile.model.User
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
        @FormUrlEncoded
        @POST("get_users.php")
        suspend fun authenticateUser(
                @Field("cpf") cpf: String,
                @Field("password") password: String
        ): AuthResponse

}
