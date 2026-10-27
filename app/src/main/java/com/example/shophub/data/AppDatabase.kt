package com.example.shophub.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.shophub.model.Category
import com.example.shophub.model.Product

@Database(
    entities = [Category::class, Product::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shophub_database"
                )
                    .addCallback(SeedCallback)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // Dados iniciais (os mesmos 3 categorias/3 produtos do Trabalho 2).
    // onCreate só roda UMA vez, quando o arquivo do banco é criado pela primeira vez;
    // por isso, o que o usuário apagar/editar depois continua apagado/editado.
    private object SeedCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            db.execSQL("INSERT INTO categorias (id, name, description, featured) VALUES (1, 'Eletrônicos', 'Produtos para casa, trabalho e entretenimento.', 1)")
            db.execSQL("INSERT INTO categorias (id, name, description, featured) VALUES (2, 'Casa', 'Itens úteis para organização e conforto.', 0)")
            db.execSQL("INSERT INTO categorias (id, name, description, featured) VALUES (3, 'Estudos', 'Materiais e acessórios para a rotina acadêmica.', 1)")

            db.execSQL("INSERT INTO produtos (id, name, price, categoryId, description, isFavorite) VALUES (1, 'Headset Bluetooth', '199.90', 1, 'Headset sem fio com boa autonomia e microfone integrado.', 1)")
            db.execSQL("INSERT INTO produtos (id, name, price, categoryId, description, isFavorite) VALUES (2, 'Luminária de Mesa', '89.90', 2, 'Luminária compacta para leitura e estudos noturnos.', 0)")
            db.execSQL("INSERT INTO produtos (id, name, price, categoryId, description, isFavorite) VALUES (3, 'Caderno Inteligente', '45.50', 3, 'Caderno reutilizável para organização de conteúdos.', 0)")
        }
    }
}
