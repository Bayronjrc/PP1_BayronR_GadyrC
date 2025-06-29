# ========================================
# CÓDIGO MIPS GENERADO AUTOMÁTICAMENTE
# Compilador - Proyecto 3
# Autores: Bayron Rodríguez & Gadir Calderón
# ========================================

.data
    # Strings del sistema
    nl:           .asciiz "\n"
    prompt_int:   .asciiz "Ingrese un entero: "
    prompt_float: .asciiz "Ingrese un float: "
    result_msg:   .asciiz "Resultado: "
    true_str:     .asciiz "true"
    false_str:    .asciiz "false"

    # Variables del programa
    a_var: .word 0
    b_var: .word 0
    temp_var: .word 0
    resultado_var: .word 0
    contador_var: .word 0
    valor_var: .word 0
    i_var: .word 0
    j_var: .word 0
    k_var: .word 0
    t1_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/s4_intermediate.txt
main:
    # Prólogo simplificado main
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # DECLARE valor INT
    # valor = 4
    li $t0, 4
    sw $t0, valor_var

    # t1 = valor > 5
    lw $t1, valor_var
    li $t2, 5
    sgt $t0, $t1, $t2
    sw $t0, t1_var

    # IF NOT t1 GOTO L1
    lw $t0, t1_var
    beq $t0, $zero, L1

    # WRITE 1
    li $a0, 1
    jal print_int
    la $a0, nl
    jal print_string

    j L2

L1:
    # WRITE 0
    li $a0, 0
    jal print_int
    la $a0, nl
    jal print_string

L2:

# Epílogo simplificado main
exit_main:
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    li $v0, 10
    syscall


# Salida del programa
exit_program:
    li $v0, 10
    syscall
# ========================================
# FUNCIONES DEL SISTEMA
# ========================================

print_int:
    li $v0, 1
    syscall
    jr $ra

print_string:
    li $v0, 4
    syscall
    jr $ra

read_int:
    li $v0, 5
    syscall
    jr $ra

read_float:
    li $v0, 6
    syscall
    jr $ra

